import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.feature.{StringIndexer, VectorAssembler}
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder}


object LogisticRegression {
  val Pre = new PreProcess

  def main(args: Array[String]): Unit = {

    // Load data
    val cleanDf = Pre.process(Pre.loadData("./cs-training.csv")).cache()

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
      .setRawPredictionCol("prediction")
      .setMetricName("areaUnderROC")

    // split data set training and test
    // training data set - 70%
    // test data set - 30%
    val seed = 1225
    val Array(trainingData, testData) = labelDf.randomSplit(Array(0.7, 0.3), seed)

    // train logistic regression model with training data set
    val logisticRegression = new LogisticRegression()
      .setMaxIter(100)
      .setRegParam(0.02)
      .setElasticNetParam(0.8)

    val logisticRegressionModel = logisticRegression.fit(trainingData)

    val predictionDf = logisticRegressionModel.transform(testData)
    //predictionDf.show(10)
    // measure the accuracy
    val accuracy = evaluator.evaluate(predictionDf)
    println(s"Accuracy = ${accuracy}")

    // we run marksDf on the pipeline, so split marksDf
    val Array(pipelineTrainingData, pipelineTestingData) = cleanDf.randomSplit(Array(0.7, 0.3), seed)

    // VectorAssembler and StringIndexer are transformers
    // LogisticRegression is the estimator
    val stages = Array(assembler, labelIndexer, logisticRegression)

    // build pipeline
    val pipeline = new Pipeline().setStages(stages)
    val pipelineModel = pipeline.fit(pipelineTrainingData)

    // test model with test data
    val pipelinePredictionDf = pipelineModel.transform(pipelineTestingData)
    //pipelinePredictionDf.show(10)

    // measure the accuracy of pipeline model
    val pipelineAccuracy = evaluator.evaluate(pipelinePredictionDf)
    println(s"Pipeline Accuracy = ${pipelineAccuracy}")

    // parameters that needs to tune, we tune
    //  1. max buns
    //  2. max depth
    //  3. impurity
    val paramGrid = new ParamGridBuilder()
      .addGrid(logisticRegressionModel.regParam, Array(0.1, 0.01, 0.01))
      .addGrid(logisticRegressionModel.fitIntercept)
      .addGrid(logisticRegressionModel.elasticNetParam, Array(0.0, 0.5, 1.0))
      .build()

    // define cross validation stage to search through the parameters
    // K-Fold cross validation with BinaryClassificationEvaluator
    val cv = new CrossValidator()
      .setEstimator(pipeline)
      .setEvaluator(evaluator)
      .setEstimatorParamMaps(paramGrid)
      .setNumFolds(5)

    // fit will run cross validation and choose the best set of parameters
    // this will take some time to run
    val cvModel = cv.fit(pipelineTrainingData)

    // test cross validated model with test data
    val cvPredictionDf = cvModel.transform(pipelineTestingData)
    //cvPredictionDf.show(10)

    // measure the accuracy of cross validated model
    // this model is more accurate than the old model
    val cvAccuracy = evaluator.evaluate(cvPredictionDf)
    println(s"Cross-validation Accuracy = ${cvAccuracy}")


    // save model
    cvModel.write.overwrite()
      .save("./LogisticRegression_CV_model")


    // save model
    logisticRegressionModel.write.overwrite().save("./LogisticRegression_model")
    pipelineModel.write.overwrite().save("./LogisticRegression_Pipeline_model")


    Pre.spark.stop()
    println(s"Accuracy = ${accuracy}")
    println(s"Pipeline Accuracy = ${pipelineAccuracy}")
    println(s"Cross-validation Accuracy = ${cvAccuracy}")
  }
}
