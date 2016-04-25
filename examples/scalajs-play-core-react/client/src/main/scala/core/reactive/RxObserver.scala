package core.reactive

import japgolly.scalajs.react.extra.OnUnmount
import japgolly.scalajs.react.{Callback, CallbackTo, CompState, TopNode}
import rx._
import rx.ops._


trait RxObserver extends OnUnmount {
  def observe[S](rx: Rx[S])($: CompState.ReadCallbackWriteCallbackOps[S]) =
    CallbackTo(rx foreach (r => $.setState(r).runNow())) >>= (obs => onUnmount(Callback(obs.kill())))

  def observeT[R, S](rx: Rx[R])(xform: (R) => S)($: CompState.ReadCallbackWriteCallbackOps[S]) =
    CallbackTo(rx foreach (r => $.setState(xform(r)).runNow())) >>= (obs => onUnmount(Callback(obs.kill())))

  def observeCB[T](rx: Rx[T])(f: (T) => Callback) =
    CallbackTo(rx foreach (r => f(r).runNow())) >>= (obs => onUnmount(Callback(obs.kill())))

  def clearAllObservations = unmount
}

object RxObserver {
  def install[P, S, B <: RxObserver, N <: TopNode] = OnUnmount.install[P, S, B, N]
}
