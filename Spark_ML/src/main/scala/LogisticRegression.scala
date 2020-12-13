import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.feature.{StringIndexer, VectorAssembler}

object LogisticRegression {
  val Pre = new PreProcess

  def main(args: Array[String]): Unit = {
    // Load data
    val cleanDf_tmp = Pre.process(Pre.loadData("./cs-training.csv")).cache()
    val cleanDf = Pre.upSample(cleanDf_tmp).cache()

    val cols = Array(
      "CreditUsage", "Age", "PastDue_30_59", "DebtRatio",
      "MonthlyIncome", "NumberOfOpenCreditLinesAndLoans", "PastDue_90",
      "NumberRealEstateLoansOrLines", "PastDue_60_89", "Dependents"
    )
    // Include all features into a vector
    val assembler = new VectorAssembler()
      .setInputCols(cols)
      .setOutputCol("features")

    // Define new 'label' column with 'result' column by labelIndexer
    val labelIndexer = new StringIndexer()
      .setInputCol("Target")
      .setOutputCol("label")

    // Evaluate model with area under ROC
    val evaluator = new BinaryClassificationEvaluator()
      .setLabelCol("label")
      .setMetricName("areaUnderROC")
      .setRawPredictionCol("rawPrediction")

    // Split data set training and test
    // training data set - 70%
    // test data set - 30%
    val seed = 1225
    val Array(pipelineTrainingData, pipelineTestingData) = cleanDf.randomSplit(Array(0.7, 0.3), seed)

    // New model
    val logisticRegressionModel = new LogisticRegression()
      .setMaxIter(20)
      .setRegParam(0.001)
      .setElasticNetParam(0.001)
      .setStandardization(true)
      .setTol(1E-10)
      .setThreshold(0.45)

    // Build the Machine Learning/ Logistic Regression Model
    // VectorAssembler and StringIndexer are transformers
    val stages = Array(assembler, labelIndexer, logisticRegressionModel)

    // Build pipeline
    val pipeline = new Pipeline().setStages(stages)

    // Train model
    val pipelineModel = pipeline.fit(pipelineTrainingData)

    // Test model with test data
    val pipelinePredictionDf = pipelineModel.transform(pipelineTestingData)

    // Measure the accuracy
    val pipelineAccuracy = evaluator.evaluate(pipelinePredictionDf)
    println(s"Logistic Regression Pipeline Accuracy = ${pipelineAccuracy}")

    // Save model
    // pipelineModel.write.overwrite().save("./LogisticRegression_Pipeline_model")
    Pre.spark.stop()
  }
}
