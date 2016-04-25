package layout


import japgolly.scalajs.react.ReactElement
import japgolly.scalajs.react.extra.router.{Resolution, RouterCtl}
import router.ApplicationRouter.Loc

/**
  * Created by Janos on 12/9/2015.
  */
trait Layout {
  def layout(c: RouterCtl[Loc], r: Resolution[Loc]): ReactElement
}
