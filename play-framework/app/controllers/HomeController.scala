package controllers

import javax.inject._
import javax.inject.Singleton
import play.api._
import play.api.mvc._
import models._

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

  def predict() = Action {  implicit request: Request[AnyContent] =>
    Ok(views.html.predict(BasicForm.form))
  }

  def simpleFormPost() = Action { implicit request =>
    BasicForm.form.bindFromRequest.fold(
      formWithErrors => {
        // binding failure, you retrieve the form containing errors:
        BadRequest(views.html.predict(formWithErrors))
      },
      formData => {
        val formData: BasicForm = BasicForm.form.bindFromRequest.get
        println(formData)
        val result1 = predictLR(formData)
        val result2 = predictRF(formData)
        Ok(views.html.predict(BasicForm.form, result1, result2))
      }
    )
  }

  def predictLR(formData: Any) : String = {
    0.85.toString
  }

  def predictRF(formData: Any) : String = {
    "Yes"
  }
}
