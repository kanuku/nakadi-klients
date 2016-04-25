package controllers

import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DemoController extends Controller {

  def index = Action.async(Future(Ok(views.html.index("SPA tutorial"))))
}
