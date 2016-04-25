package controllers

import java.nio.ByteBuffer

import boopickle.Default._
import boopickle.Pickler
import play.api.mvc.{Controller, RawBuffer, Request}

import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Autowire router
  */
object Router extends autowire.Server[ByteBuffer, Pickler, Pickler] {

  override def read[R: Pickler](p: ByteBuffer) = Unpickle[R].fromBytes(p)

  override def write[R: Pickler](r: R) = Pickle.intoBytes(r)
}

trait ServiceController extends Controller {
  /**
    * Helper for internal routing
    * @param path
    * @param request
    * @param router
    * @return
    */
  protected def internalRoute(path: String, request: Request[RawBuffer])(router: => Router.Router) = {
    val b = request.body.asBytes(parse.UNLIMITED).get
    router(
      autowire.Core.Request(path.split("/"), Unpickle[Map[String, ByteBuffer]].fromBytes(ByteBuffer.wrap(b)))
    ).map(buffer => {
      val data = Array.ofDim[Byte](buffer.remaining())
      buffer.get(data)
      Ok(data)
    })
  }
}
