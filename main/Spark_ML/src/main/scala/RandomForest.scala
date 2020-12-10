import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.RandomForestClassifier
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.feature.{StringIndexer, VectorAssembler}

object RandomForest {
  // Data Pre-Processing
  val Pre = new PreProcess

  def main(args: Array[String]): Unit = {

    // Load data
    val cleanDf = Pre.process(Pre.loadData("./dataset/cs-training.csv")).cache()

    // Random Forest

    val cols = Array(
      "CreditUsage", "Age", "PastDue_30_59", "DebtRatio",
      "MonthlyIncome", "NumberOfOpenCreditLinesAndLoans", "PastDue_90",
      "NumberRealEstateLoansOrLines", "PastDue_60_89", "Dependents"
    )

    // Include all features into a vector
    val assembler = new VectorAssembler()
      .setInputCols(cols)
      .setOutputCol("features")
    val featureDf = assembler.transform(cleanDf)

    val labelIndexer = new StringIndexer()
      .setInputCol("Target")
      .setOutputCol("label")
    val labelDf = labelIndexer.fit(featureDf).transform(featureDf)

    // evaluate model with area under ROC
    val evaluator = new BinaryClassificationEvaluator()
      .setLabelCol("label")
      .setMetricName("areaUnderROC")
      .setRawPredictionCol("rawPrediction")

    // split data set training and test
    // training data set - 70%
    // test data set - 30%
    val seed = 1225
    val Array(trainingData, testData) = labelDf.randomSplit(Array(0.7, 0.3), seed)

    // train Random Forest model with training data set
    val randomForestClassifier = new RandomForestClassifier()
      .setImpurity("gini")
      .setMaxDepth(3)
      .setNumTrees(20)
      .setFeatureSubsetStrategy("auto")
      .setSeed(seed)

    val randomForestModel = randomForestClassifier.fit(trainingData)
    // println(randomForestModel.toDebugString)

    val predictionDf = randomForestModel.transform(testData)
    // predictionDf.show(10)
    // measure the accuracy
    val accuracy = evaluator.evaluate(predictionDf)
    println(s"Accuracy = ${accuracy}")

    // Build the Machine Learning/ Random Forest Model
    val Array(pipelineTrainingData, pipelineTestingData) = cleanDf.randomSplit(Array(0.7, 0.3), seed)

    // VectorAssembler and StringIndexer are transformers
    val stages = Array(assembler, labelIndexer, randomForestClassifier)

    // build pipeline
    val pipeline = new Pipeline().setStages(stages)
    val pipelineModel = pipeline.fit(pipelineTrainingData)

    // test model with test data
    val pipelinePredictionDf = pipelineModel.transform(pipelineTestingData)
    // pipelinePredictionDf.show(10)

    // measure the accuracy
    val pipelineAccuracy = evaluator.evaluate(pipelinePredictionDf)
    println(s"Pipeline Accuracy = ${pipelineAccuracy}")

    // save model
    randomForestModel.write.overwrite().save("./RandomForest_model")

    pipelineModel.write.overwrite().save("./RandomForest_Pipeline_model")

    Pre.spark.stop()
  }
}
