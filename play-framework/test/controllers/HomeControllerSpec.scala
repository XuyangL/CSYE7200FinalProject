package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class HomeControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "HomeController GET" should {

    "render the index page from a new instance of controller" in {
      val controller = new HomeController(stubControllerComponents())
      val home = controller.index().apply(FakeRequest(GET, "/index"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
    }

    "render the aboutus page from a new instance of controller" in {
      val controller = new HomeController(stubControllerComponents())
      val home = controller.index().apply(FakeRequest(GET, "/aboutus"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
    }

    "render the predict page from a new instance of controller" in {
      val controller = new HomeController(stubControllerComponents())
      val home = controller.index().apply(FakeRequest(GET, "/predict"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
    }

    "render the record page from a new instance of controller" in {
      val controller = new HomeController(stubControllerComponents())
      val home = controller.index().apply(FakeRequest(GET, "/record"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
    }

    "render the recorddatial page from a new instance of controller" in {
      val controller = new HomeController(stubControllerComponents())
      val home = controller.index().apply(FakeRequest(GET, "/recorddatial"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
    }
  }
}
