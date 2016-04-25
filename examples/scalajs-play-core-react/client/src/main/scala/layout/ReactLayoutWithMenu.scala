package layout

import component.{Menu, Route}
import core.material._
import japgolly.scalajs.react.extra.OnUnmount
import japgolly.scalajs.react.extra.router.{Path, RouterCtl}
import japgolly.scalajs.react.vdom.TagMod
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{BackendScope, PropsChildren, ReactComponentB, ReactNode}
import router.ApplicationRouter.Loc

case class HeaderConfig(
                         router: RouterCtl[Loc] = null,
                         title: String = "Sample Title",
                         menu: List[Menu] = List(),
                         topLinks: Option[TagMod] = None
                       )

object ReactLayoutWithMenu {

  class Backend($: BackendScope[HeaderConfig, Unit]) extends OnUnmount {
    def render(P: HeaderConfig, C: PropsChildren) = {
      <.div(^.cls := "mdl-layout__container")(
        <.div(^.cls := "mdl-layout mdl-js-layout mdl-layout--fixed-drawer")(
          <.header(^.cls := "mdl-layout__header--transparent")(
            <.div(^.cls := "mdl-layout__header-row")(
              <.span(^.cls := "mdl-layout-title")(P.title)
            ),
            P.topLinks.map(i => i)
          ),
          <.div(^.cls := "mdl-layout__drawer")(
            <.span(^.cls := "mdl-layout-title")(P.title),
            <.nav(^.cls := "mdl-navigation")(
              P.menu.map(menu => menu.`type` match {
                case Route if P.router != null =>
                  <.a(
                    ^.cls := "mdl-navigation__link",
                    ^.onClick --> {
                      P.router.byPath.set(Path(menu.path))
                    }
                  )(menu.text)
                case _ =>
                  <.a(^.cls := "mdl-navigation__link", ^.href := menu.path)(menu.text)
              })
            )
          ),
          <.main(^.cls := "mdl-layout__content")(C)
        )
      ).material(true)
    }
  }

  val component = ReactComponentB[HeaderConfig]("Application-Header")
    .renderBackend[Backend]
    .build

  def apply(config: HeaderConfig)(nodes: ReactNode*) = component(config, nodes: _*)
}
