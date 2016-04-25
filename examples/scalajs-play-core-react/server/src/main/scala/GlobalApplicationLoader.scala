import com.softwaremill.macwire._
import controllers.Assets
import modules.{Controllers, Service}
import play.api.ApplicationLoader.Context
import play.api.routing.Router
import play.api.routing.sird._
import play.api.{Application, ApplicationLoader, BuiltInComponents, BuiltInComponentsFromContext}

/**
  * Global application context
  */
class GlobalApplicationLoader extends ApplicationLoader {
  override def load(context: Context): Application = (new BuiltInComponentFromContextWithPlayWorkaround(context) with ApplicationComponents).application
}

abstract class BuiltInComponentFromContextWithPlayWorkaround(context: Context) extends BuiltInComponentsFromContext(context) {

  import play.api.inject.{Injector, NewInstanceInjector, SimpleInjector}
  import play.api.libs.Files.DefaultTemporaryFileCreator

  lazy val defaultTemporaryFileCreator = new DefaultTemporaryFileCreator(applicationLifecycle)

  override lazy val injector: Injector = new SimpleInjector(NewInstanceInjector) + router + crypto + httpConfiguration + defaultTemporaryFileCreator
}

trait ApplicationComponents extends BuiltInComponents with Controllers with Service {
  lazy val assets: Assets = wire[Assets]
  lazy val router: Router = Router.from {
    case GET(p"/") => applicationController.index
    case GET(p"/assets/$file*") => Assets.versioned(path = "/public", file = file)
    case POST(p"/api/sample/$path*") => apiController.sampleApi(path)
  }
}


