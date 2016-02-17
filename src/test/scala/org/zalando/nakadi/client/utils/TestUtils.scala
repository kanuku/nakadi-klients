package org.zalando.nakadi.client.utils

class TestUtils {
  def toScalaMap(m: java.util.Map[String, Any]): Map[String, Any] = {
    import scala.collection.JavaConversions._
    m.toMap
  }
}
