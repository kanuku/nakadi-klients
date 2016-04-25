package core

import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{ReactComponentU, TopNode}

package object material {
  implicit class MaterialAble(val elem: ReactTag) extends AnyVal {
    def material: ReactComponentU[(ReactTag, Boolean), Unit, Unit, TopNode] = {
      MaterialComponent((elem, false))
    }

    def material(children: Boolean): ReactComponentU[(ReactTag, Boolean), Unit, Unit, TopNode] = {
      MaterialComponent((elem, children))
    }
  }
}
