package org.zalando.nakadi.client

import scala.concurrent.duration.DurationInt
import org.scalatest.{ Matchers, WordSpec }
import org.zalando.nakadi.client.model._
import org.zalando.nakadi.client.model.DefaultMarshaller
import akka.actor.ActorSystem
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.marshalling.Marshaller._
import akka.stream.Materializer
import akka.http.scaladsl.unmarshalling._
import akka.http.scaladsl.model._
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.marshalling.GenericMarshallers
import akka.http.scaladsl.marshalling.Marshaller
import org.zalando.nakadi.client.model.Event
import org.zalando.nakadi.client.model._

class DefaultMarshallerTest extends WordSpec with Matchers with DefaultMarshaller {
  implicit val system = ActorSystem("rest-server")
  implicit val dispatcher = system.dispatcher
  implicit val materializer: Materializer = ActorMaterializer()

  import org.zalando.nakadi.client.util.SimpleModelFactory._

  /**
   * Tests
   */
  "All model Objects " must {
    val testName = "marshal an unmarshall without any problems"
    s"$testName(eventMetadata)" in {
      checkSerializationProcess("eventMetadata", eventMetadata)
    }
    s"$testName(problem)" in {
      checkSerializationProcess("problem", problem)
    }
    s"$testName(metrics)" in {
      checkSerializationProcess("metrics", metrics)
    }
    s"$testName(partition)" in {
      checkSerializationProcess("partition", partition)
    }
    s"$testName(cursor)" in {
      checkSerializationProcess("cursor", cursor)
    }
    s"$testName(eventTypeSchema)" in {
      checkSerializationProcess("eventTypeSchema", eventTypeSchema)
    }
    s"$testName(eventValidationStrategy)" in {
      checkSerializationProcess("eventValidationStrategy", eventValidationStrategy)
    }
    s"$testName(partitionResolutionStrategy)" in {
      checkSerializationProcess("partitionResolutionStrategy", partitionResolutionStrategy)
    }
    s"$testName(eventEnrichmentStrategy)" in {
      checkSerializationProcess("eventEnrichmentStrategy", partitionResolutionStrategy)
    }
    s"$testName(businessEvent)" in {
      checkSerializationProcess("businessEvent", businessEvent)
    }
    s"$testName(dataChangeEventQualifier)" in {
      checkSerializationProcess("dataChangeEventQualifier", dataChangeEventQualifier)
    }
    s"$testName(dataChangeEvent)" in {
      checkSerializationProcess("dataChangeEvent", dataChangeEvent)
    }
    s"$testName(eventType)" in {
      checkSerializationProcess("eventType", eventType)
    }
    s"$testName(event)" in {
      checkSerializationProcess("event", event)
    }
    s"$testName(eventStreamBatch)" in {
      checkSerializationProcess("eventStreamBatch", eventStreamBatch)
    }

  }
  def checkSerializationProcess[T](key: String, obj: T)(implicit m: Marshaller[T, String], um: Unmarshaller[String, T]) {
    val futureEntity = Marshal(obj).to[String]
    val stringResult = Await.result(futureEntity, 1.second) // don't block in non-test code!
    val futureResult = Unmarshal(stringResult).to[T]
    val eventResult = Await.result(futureResult, 1.second) // don't block in non-test code!
    assert(eventResult == obj, s"Failed to marshall $key")
  }

}