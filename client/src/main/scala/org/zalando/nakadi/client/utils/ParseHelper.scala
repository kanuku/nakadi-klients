package org.zalando.nakadi.client.utils

import spray.json._
import DefaultJsonProtocol._
import org.zalando.nakadi.client.model.DataOperation

object ParseHelper {
  /*
  * From (link expired!) -> https://tech.mendix.com/scala/2014/09/28/scala-nested-maps-to-json/
  */
  implicit object MapJsonFormat extends JsonFormat[Map[String, Any]] {
    def write(m: Map[String, Any]) = {
      JsObject(m.mapValues {
        case s: String => JsString(s)
        case v: Int => JsNumber(v) // will get output without decimal point
        case v: Long => JsNumber(v)
        case v: Double => JsNumber(v)
        case b: Boolean => JsBoolean(b)
        case mm: Map[String @unchecked, Any @unchecked] => write(mm)
        case x => serializationError(s"Unexpected value within Map[String,Any] values (cannot convert to JSON): $x")
      })
    }

    def read(jsv: JsValue) = jsv match {
      case jso: JsObject => readMap(jso)
      case v => deserializationError("Expected JsObject, but got: " + v)
    }

    // Note: Makes sense having this as a separate function (not embedded in 'read') because our recursion can now
    //      use 'JsObject's directly.
    //
    private def readMap(jso: JsObject): Map[String, Any] = jso.fields.mapValues {
      case JsString(s) => s
      case JsNumber(d) if d.intValue == d => d.intValue
      case JsNumber(d) if d.longValue == d => d.longValue
      case JsNumber(d) => d
      case JsBoolean(b) => b
      case v: JsObject => readMap(v)
      case v => deserializationError("Unexpected value within JsObject: " + v)
    }
  }

  implicit object DataOperationFormat extends JsonFormat[DataOperation.Value] {
    def write(in: DataOperation.Value) = JsString(in.toString())
    def read(json: JsValue) = json match {
      case JsString(s) => DataOperation.withName(s)
      case somethingElse => deserializationError("Unexpected value four our Enumerator: " + somethingElse)
    }
  }
  
    implicit def listFormat[T :JsonFormat] = new JsonFormat[T] {
    def write(in: T) = write(in)
    def read(value: JsValue) = value match {
      case element:JsObject => element.convertTo[T]
      case _ => deserializationError("A JsObject was expected!!")
    }
  }
}