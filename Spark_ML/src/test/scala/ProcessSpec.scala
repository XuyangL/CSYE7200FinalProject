import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import org.scalatest.{Matchers, FlatSpec}

class ProcessSpec extends FlatSpec with Matchers{
  Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
  private val master = "local[*]"
  private val appName = "testing"
  var spark: SparkSession = _

  behavior of "PreProcess"

  it should "work for loadData" in {
    spark = new SparkSession.Builder().appName(appName).master(master).getOrCreate()
    val sc = spark.sqlContext
    import sc.implicits._
    val Pre = new PreProcess
    val loadTestDf = Pre.loadData("./testData/loadTest.csv")
    val sampleDf = Seq(
      (1, 1, 0.924215157, 47, 2, 0.313124867, 4700, 7, 5, 0, 1, 3),
      (2, 1, 0.9999999, 49, 3, 2953.0, 6670, 6, 2, 2, 5, 0),
      (3, 1, 0.9999999, 49, 1, 2977.0, 6670, 2, 4, 2, 1, 0),
      (4, 1, 0.9999999, 33, 1,0.638233054, 2625, 2, 3, 1, 1, 1),
      (5, 1, 0.944256968, 34, 1,0.431991581, 3800, 4, 2, 0, 1, 1),
      (6, 0, 0.005721218, 57, 0, 0.001333156, 7500, 3, 0, 0, 0, 0),
      (7, 0, 0.002034966, 51, 2, 0.337132573, 5000, 13, 0, 2, 1, 1),
      (8, 0, 0.631793585, 33, 0, 0.484621155, 5331, 5, 0, 1, 0, 2),
      (9, 0, 0.157507611, 58, 0, 0.217778586, 5500, 8, 0, 1, 0, 0),
      (10, 0, 0.593335676, 41, 0, 0.279039752, 7747, 6, 0, 1, 0, 2)
    ).toDF(
      "_c0", "SeriousDlqin2yrs", "RevolvingUtilizationOfUnsecuredLines", "age",
      "NumberOfTime30-59DaysPastDueNotWorse", "DebtRatio", "MonthlyIncome",
      "NumberOfOpenCreditLinesAndLoans", "NumberOfTimes90DaysLate", "NumberRealEstateLoansOrLines",
      "NumberOfTime60-89DaysPastDueNotWorse", "NumberOfDependents"
    )
    sampleDf.count() should equal (loadTestDf.count())
    sampleDf.except(loadTestDf).count() should equal(0)
  }

  it should "work for process" in {
    spark = new SparkSession.Builder().appName(appName).master(master).getOrCreate()
    val Pre = new PreProcess
    val processTestDf = Pre.process(Pre.loadData("./testData/processTest.csv"))
    val schema = StructType(List(
        StructField("_c0", IntegerType, true),
        StructField("SeriousDlqin2yrs", IntegerType, true),
        StructField("RevolvingUtilizationOfUnsecuredLines", DoubleType, true),
        StructField("age", IntegerType, true),
        StructField("NumberOfTime30-59DaysPastDueNotWorse", IntegerType,  true),
        StructField("DebtRatio", DoubleType, true),
        StructField("MonthlyIncome", IntegerType, true),
        StructField("NumberOfOpenCreditLinesAndLoans", IntegerType, true),
        StructField("NumberOfTimes90DaysLate", IntegerType, true),
        StructField("NumberRealEstateLoansOrLines", IntegerType, true),
        StructField("NumberOfTime60-89DaysPastDueNotWorse", IntegerType, true),
        StructField("NumberOfDependents", IntegerType, true)
    ))
    val data = Seq(
      Row(1,1,0.1,null,1,0.1,null,0,0,0,0,null),
      Row(2,0,0.2,20,2,0.2,2000,4,0,0,0,1),
      Row(3,0,0.6,60,6,0.6,6000,2,1,0,0,1),
      Row(4,0,0.3,30,3,0.3,3000,5,0,0,0,3),
      Row(5,0,0.5,50,5,0.5,5000,7,0,1,0,3)
    )
    val sampleDf = spark.createDataFrame(spark.sparkContext.parallelize(data), schema)
    val processSample = Pre.process(sampleDf)
    processSample.except(processTestDf).count() should equal(0)
    val processRow = processSample.rdd.take(1)
    processRow(0)(2) should equal(40)
    processRow(0)(5) should equal(4000)
    processRow(0)(10) should equal(1)
  }

  it should "work for upSample" in {
    spark = new SparkSession.Builder().appName(appName).master(master).getOrCreate()
    val sc = spark.sqlContext
    import sc.implicits._
    val Pre = new PreProcess
    val data = Seq(
      (1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
      (1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
      (0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
      (0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
      (0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
      (0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
      (0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
      (0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
      (0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
      (0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
      (0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
      (0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
      (0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
      (0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
      (0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
      (0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
      (0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
      (0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
      (0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
      (0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    ).toDF("Target", "CreditUsage", "Age", "PastDue_30_59", "DebtRatio",
        "MonthlyIncome", "NumberOfOpenCreditLinesAndLoans", "PastDue_90",
        "NumberRealEstateLoansOrLines", "PastDue_60_89", "Dependents"
    )
    val SampleDf = Pre.upSample(data)
    SampleDf.count() should equal(26)
    SampleDf.createOrReplaceTempView("sample")
    val target_1 = spark.sql("SELECT * FROM sample WHERE Target = 1")
    val target_0 = spark.sql("SELECT * FROM sample WHERE Target = 0")
    target_1.count() should equal(8)
    target_0.count() should equal(18)
  }
}
