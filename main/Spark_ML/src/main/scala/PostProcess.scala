import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.SparkContext
import org.apache.spark.sql.{DataFrame, SparkSession}

class PostProcess {
  val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()

  // Save result dataframe to csv file
  def toCSV(result: DataFrame, path: String): Unit = {
    try{
      result.repartition(1).write.format("com.databricks.spark.csv").option("header", "true").save(path)
    }catch{
      case e1: IllegalArgumentException=> print("fail to save the data into csv:"+e1)
      case e2: RuntimeException => print("fail to save the data into csv:"+e2)
      case e3: Exception =>print("fail to save the data into csv:"+e3)
    }
  }

  // Rename csv file and clean out folder
  def cleanOutput(oldFile: String, newFile: String): Unit = {
    val sc: SparkContext = spark.sparkContext
    val fs = FileSystem.get(sc.hadoopConfiguration)
    val file = fs.globStatus(new Path(oldFile + "/part*"))(0).getPath().getName()
    fs.rename(new Path(oldFile + "/" + file), new Path(newFile))
    fs.delete(new Path(oldFile), true)
  }
}
