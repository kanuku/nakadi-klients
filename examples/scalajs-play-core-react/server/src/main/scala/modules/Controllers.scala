package modules

import com.softwaremill.macwire._
import controllers.{ApiController, DemoController}
import play.api.BuiltInComponents

/**
  * Created by Janos on 12/9/2015.
  */
trait Controllers extends BuiltInComponents {
  lazy val applicationController = wire[DemoController]
  lazy val apiController = wire[ApiController]
}
