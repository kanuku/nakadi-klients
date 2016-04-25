package core.service

import java.nio.ByteBuffer

import boopickle.Default._
import org.scalajs.dom

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js.typedarray.{ArrayBuffer, TypedArrayBuffer}


/**
  * Abstract Autowire client for api call
  *
  * It's send the API call into /api/{name} path and process the response
  *
  * @param name the api name in the server side (example: sample will call /api/sample)
  */
abstract class AbstractClient(name: String) extends autowire.Client[ByteBuffer, Pickler, Pickler] {
  override def doCall(req: Request): Future[ByteBuffer] = dom.ext.Ajax.post(
    url = s"/api/${name}/${req.path.mkString("/")}",
    data = Pickle.intoBytes(req.args),
    responseType = "arraybuffer",
    headers = Map("Content-Type" -> "application/octet-stream")
  ).map(r => TypedArrayBuffer.wrap(r.response.asInstanceOf[ArrayBuffer]))

  override def read[Result: Pickler](p: ByteBuffer) = Unpickle[Result].fromBytes(p)

  override def write[Result: Pickler](r: Result) = Pickle.intoBytes(r)
}
