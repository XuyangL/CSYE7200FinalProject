object TestPre {

  val Pre = new PreProcess

  def main(args: Array[String]): Unit = {
    // Load data
    val cleanDf = Pre.process(Pre.loadData("./cs-training.csv")).cache()
    Pre.spark.stop()
  }
}