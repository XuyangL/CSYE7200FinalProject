package controllers

import javax.inject._
import javax.inject.Singleton
import play.api._
import play.api.mvc._
import models._
import play.api.data.Form
import play.api.data.Forms._
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.sql.SparkSession

import scala.concurrent.Future

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController with play.api.i18n.I18nSupport{

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def aboutus() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.aboutus())
  }

  def predict() = Action {  implicit request: Request[AnyContent] =>
    Ok(views.html.predict(BasicForm.form))
  }

  def decision() = Action {  implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }


  def simpleFormPost() = Action { implicit request =>

      val errorFunction = {formWithErrors: Form[BasicForm] =>
        BadRequest(views.html.predict(formWithErrors))
      }

      val successFunction = { formData: BasicForm =>
        val formData: BasicForm = BasicForm.form.bindFromRequest.get
        val predictData = BasicForm.unapply(formData).get

        val lr = predictLR(predictData)
        val rf = predictRF(predictData)

        val col = List("CreditUsage", "Age", "PastDue_30_59", "DebtRatio", "MonthlyIncome", "NumberOfOpenCreditLinesAndLoans", "PastDue_90", "NumberRealEstateLoansOrLines", "PastDue_60_89", "Dependents")
        val predictDataList = List(predictData._1, predictData._2, predictData._3, predictData._4, predictData._5, predictData._6, predictData._7, predictData._8, predictData._9, predictData._10).map(_ toString)
        val target = col.zip(predictDataList)

        Ok(views.html.predictresult(target, rf, lr))
      }

      BasicForm.form.bindFromRequest.fold(errorFunction, successFunction)
  }

  def predictLR(predictData: (BigDecimal, Int, Int, BigDecimal, Int, Int, Int, Int, Int, Int)) : String = {
    0.85.toString
  }

  def predictRF(predictData: (BigDecimal, Int, Int, BigDecimal, Int, Int, Int, Int, Int, Int)) : String = {
//    val spark = SparkSession.builder().appName("Analyze").master("local[*]").getOrCreate()
//    import spark.implicits._
//    val rfLoaded = PipelineModel.load("../main/Spark_ML/RandomForest_Pipeline_model")
//    val testDf = Seq(predictData).toDF("CreditUsage", "Age", "PastDue_30_59", "DebtRatio", "MonthlyIncome", "NumberOfOpenCreditLinesAndLoans", "PastDue_90", "NumberRealEstateLoansOrLines", "PastDue_60_89", "Dependents")
//    val testResDf = rfLoaded.transform(testDf)
//    val output = testResDf.select($"prediction").rdd.collect().map(x => x.getDouble(0))
//    output(0).toString() match {
//      case "0.0" => "Yes"
//      case "1.0" => "No"
//    }
    "Yes"
  }
}
