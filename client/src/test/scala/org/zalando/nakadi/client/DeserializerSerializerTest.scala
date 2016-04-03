package org.zalando.nakadi.client

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import org.scalatest.{ Matchers, WordSpec }
import org.zalando.nakadi.client.model.SprayJsonMarshaller
import org.zalando.nakadi.client.util.AkkaConfig
import akka.http.scaladsl.marshalling.{ Marshal, Marshaller }
import akka.http.scaladsl.unmarshalling.{ Unmarshal, Unmarshaller }
import akka.stream.Materializer
import org.zalando.nakadi.client.util.TestJsonEntity
import org.zalando.nakadi.client.model.EventType

class DeserializerSerializerTest extends WordSpec with Matchers with SprayJsonMarshaller with AkkaConfig {
  import TestJsonEntity._
  "When a json entity is unmarshalled and marshalled it" should {
    val testName = "always result in the same entity"
    s"$testName(event)" in {
      checkDeserializationProcessSerialization[EventType]("event", events)
    }
  }

  def checkDeserializationProcessSerialization[T](key: String, value: String)(implicit m: Marshaller[T, String], um: Unmarshaller[String, T]) {
    val futureScalaEntity = Unmarshal(value).to[T]
    val scalaEntity = Await.result(futureScalaEntity, 1.second)
    val futureJsonEntity = Marshal(value).to[String]
    val jsonEntity = Await.result(futureScalaEntity, 1.second)
    assert(jsonEntity == value, s"Failed to marshall $key")
  }

}