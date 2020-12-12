import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.types._

class PreProcess {
  val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()

  val schema = StructType(
      StructField("_c0", IntegerType, true) ::
      StructField("SeriousDlqin2yrs", IntegerType, true) ::
      StructField("RevolvingUtilizationOfUnsecuredLines", DoubleType, true) ::
      StructField("age", IntegerType, true) ::
      StructField("NumberOfTime30-59DaysPastDueNotWorse", IntegerType,  true) ::
      StructField("DebtRatio", DoubleType, true) ::
      StructField("MonthlyIncome", IntegerType, true) ::
      StructField("NumberOfOpenCreditLinesAndLoans", IntegerType, true) ::
      StructField("NumberOfTimes90DaysLate", IntegerType, true) ::
      StructField("NumberRealEstateLoansOrLines", IntegerType, true) ::
      StructField("NumberOfTime60-89DaysPastDueNotWorse", IntegerType, true) ::
      StructField("NumberOfDependents", IntegerType, true) ::
      Nil
  )

  // Load data
  def loadData(file: String) = {
    val rawDf = spark.read
      .option("header", "true")
      .option("delimiter", ",")
      .option("mode", "DROPMALFORMED")
      .schema(schema)
      .format("com.databricks.spark.csv")
      .load(file)
      .toDF()
      .cache()

    rawDf
  }

  // Process and clean Data
  def process(df: DataFrame): DataFrame = {
    // Rename Column and transfer the data type
    val cleanTypeDf = df
      .withColumnRenamed("SeriousDlqin2yrs", "Target")
      .withColumnRenamed("RevolvingUtilizationOfUnsecuredLines", "CreditUsage")
      .withColumnRenamed("NumberOfTime30-59DaysPastDueNotWorse", "PastDue_30_59")
      .withColumnRenamed("NumberOfTimes90DaysLate", "PastDue_90")
      .withColumnRenamed("NumberOfTime60-89DaysPastDueNotWorse", "PastDue_60_89")
      .withColumnRenamed("NumberOfDependents", "Dependents")
      .withColumnRenamed("age", "Age")
      .drop("_c0")

    // Fill null value in MonthlyIncome and Dependents with 0
    // val fillZeroDF = cleanTypeDF.na.fill(0)
    // Show min, max, mean, std_dev
    // cleanTypeDF.describe().show()

    // Fill null value in MonthlyIncome and Dependents with mean
    // val imputer = new Imputer().setInputCols(cleanTypeDF.columns).setOutputCols(cleanTypeDF.columns.map(c => s"${c}_imputed")).setStrategy("mean")
    // val fillZeroDF = imputer.fit(cleanTypeDF).transform(cleanTypeDF)

    cleanTypeDf.createOrReplaceTempView("cleanTemp1")
    val avg_Age = spark.sql("SELECT avg(Age) FROM cleanTemp1 WHERE Dependents >= 14").first().getDouble(0)
    val fillZeroDf1 = cleanTypeDf.na.fill(avg_Age, Seq("Age"))

    fillZeroDf1.createOrReplaceTempView("cleanTemp2")
    // val work_MonthlyIncome = spark.sql("SELECT avg(MonthlyIncome) FROM cleanTemp2 WHERE AGE > 17 and Age < 61").first().getDouble(0)
    // val senior_MonthlyIncome = spark.sql("SELECT avg(MonthlyIncome) FROM cleanTemp2 WHERE Age > 60").first().getDouble(0)
    // Result: There is no big difference between work/senior to monthly income
    val avg_MonthlyIncome = spark.sql("SELECT avg(MonthlyIncome) FROM cleanTemp2").first().getDouble(0)
    val fillZeroDf2 = fillZeroDf1.na.fill(avg_MonthlyIncome, Seq("MonthlyIncome"))

    fillZeroDf2.createOrReplaceTempView("cleanTemp3")
    val median_Dependents = spark.sql("SELECT percentile_approx(Dependents, 0.5) FROM cleanTemp3").first().getInt(0)
    val fillZeroDf3 = fillZeroDf2.na.fill(median_Dependents, Seq("Dependents"))
    println(s"--Ingestion Finish--.")
    val finalDf = fillZeroDf3.na.fill(0)
    finalDf
  }

  def upSample(df: DataFrame): DataFrame = {
    df.createOrReplaceTempView("sample")
    val target_1 = spark.sql("SELECT * FROM sample WHERE Target = 1")
    val unbalancedRatio = (100*(target_1.count().toDouble / df.count().toDouble)).round
    val sampleRatio = (30 / unbalancedRatio).toInt
    var seqDf = Seq[DataFrame]()
    for (i <- 0 to sampleRatio) {
      seqDf = target_1 +: seqDf
    }
    val reduceDf = seqDf.reduce(_ union _)
    val finalDf = df.union(reduceDf)
    finalDf
  }
}
