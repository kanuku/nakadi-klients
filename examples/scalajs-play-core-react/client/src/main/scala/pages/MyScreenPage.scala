package pages

import autowire._
import boopickle.Default._
import demo.SampleApi
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{BackendScope, Callback, ReactComponentB}
import service.SampleClient
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
/**
  * Main Screen component
  */
object MyScreenPage {

  case class State(message: String)

  class Backend($: BackendScope[Unit, State]) {
    def init() = {
      Callback {
        SampleClient[SampleApi].echo("Hi there")
          .call()
          .map(response => $.modState(_.copy(message = response)).runNow())
      }
    }

    def render() = <.div($.state.runNow().message)
  }

  val component = ReactComponentB[Unit]("MyScreenPage")
    .initialState(State(""))
    .renderBackend[Backend]
    .componentDidMount(_.backend.init())
    .buildU
}
