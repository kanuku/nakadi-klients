import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport


object SPA extends js.JSApp {
  @JSExport
  override def main(): Unit = {
    import router.ApplicationRouter._
    router() render dom.document.getElementById("container")
  }
}
