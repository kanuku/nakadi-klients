package org.zalando.nakadi.client.utils

/**
  * Created by bfriedrich on 14/02/16.
  */
class TestUtils {
  def toScalaMap(m: java.util.Map[String, Any]): Map[String, Any] = {
    import scala.collection.JavaConversions._
    m.toMap
  }
}
