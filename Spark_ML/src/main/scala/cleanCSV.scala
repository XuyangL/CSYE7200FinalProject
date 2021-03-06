

object cleanCSV {

  val Pre = new PreProcess

  val Post = new PostProcess

  def main(args: Array[String]): Unit = {
    // Load data
    val cleanDf_tmp = Pre.process(Pre.loadData("./cs-training.csv")).cache()

    val cleanDf = Pre.upSample(cleanDf_tmp).cache()

    Post.toCSV(cleanDf, "result_clean-train.csv")

    Post.cleanOutput("result_clean-train.csv", "clean-train.csv")
  }
}
