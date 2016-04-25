package router

import japgolly.scalajs.react.extra.router._
import layout.MainLayout
import pages.MyScreenPage

/**
  * Created by Janos on 12/9/2015.
  */
object ApplicationRouter {

  sealed trait Loc

  case object MainScreen extends Loc

  case object SubScreen extends Loc

  lazy val routerConfig: RouterConfig[Loc] = RouterConfigDsl[Loc].buildConfig(dsl => {
    import dsl._
    (
      emptyRule
        | staticRoute(root, MainScreen) ~> render(MyScreenPage.component())
      )
      .notFound(redirectToPage(MainScreen)(Redirect.Replace))
      .renderWith(MainLayout.layout)
      .logToConsole
  })

  val baseUrl = BaseUrl.fromWindowOrigin_/
  val router = Router(baseUrl, routerConfig)
}
