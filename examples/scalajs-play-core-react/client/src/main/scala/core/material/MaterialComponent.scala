package core.material

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.all._

import scala.scalajs.js

/**
  * Created by Janos on 12/9/2015.
  */
object MaterialComponent {

  val rc = ReactComponentB[(ReactTag, Boolean)]("MaterialComponent")
    .renderP(($, p) => {
      p._1
    })
    .componentDidMount(afterMount)
    .componentDidUpdate(afterUpdate)
    .build

  def apply(props: (ReactTag, Boolean)): ReactComponentU[(ReactTag, Boolean), Unit, Unit, TopNode] = {
    rc(props)
  }

  private def upgrade(scope: CompScope.DuringCallbackM[(ReactTag, Boolean), Unit, Unit, TopNode]): Callback = {
    js.Dynamic.global.window.componentHandler.upgradeElement(scope.getDOMNode())
    if (scope.props._2) {
      val children = scope.getDOMNode().children
      (0 until children.length).foreach(i => {
        js.Dynamic.global.window.componentHandler.upgradeElement(children(i))
      }
      )
    }
    Callback.empty
  }

  def afterMount(scope: CompScope.DuringCallbackM[(ReactTag, Boolean), Unit, Unit, TopNode]): Callback = {
    upgrade(scope)
  }

  def afterUpdate(scope: ComponentDidUpdate[(ReactTag, Boolean), Unit, Unit, TopNode]): Callback = {
    upgrade(scope.$)
  }
}
