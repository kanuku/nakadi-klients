package layout

import japgolly.scalajs.react.ReactElement
import japgolly.scalajs.react.extra.router.{Resolution, RouterCtl}
import japgolly.scalajs.react.vdom.prefix_<^._
import router.ApplicationRouter.Loc

object MainLayout extends Layout {
  override def layout(c: RouterCtl[Loc], r: Resolution[Loc]): ReactElement = <.span(ReactLayoutWithMenu(HeaderConfig(c,"Hello World Title"))(r.render()))
}
