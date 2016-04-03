package org.zalando.nakadi.client

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import org.scalatest.{ Matchers, WordSpec }
import org.zalando.nakadi.client.util.AkkaConfig
import akka.http.scaladsl.marshalling.{ Marshal, Marshaller }
import akka.http.scaladsl.unmarshalling.{ Unmarshal, Unmarshaller }
import org.zalando.nakadi.client.util.TestScalaEntity
import org.zalando.nakadi.client.model.SprayJsonMarshaller

/**
 * Tests the Marshalling and Umarshalling of the same object in a single run. It tests in this sequence: 1.Marshall and 2.Unmarshall. <br>
 * This is just a simple test that should break when Custom
 * Marshallers/Unmarshallers are used and produce different
 * unexpected results.
 */
class SerializerDeserializerTest extends WordSpec with Matchers with SprayJsonMarshaller with AkkaConfig {

  import TestScalaEntity._
  

  /**
   * Tests
   */
  "When an entity(scala object) is marshalled and unmarshalled it" should {
    val testName = "always result in the same entity"
    s"$testName(eventMetadata)" in {
      checkSerializationDeserializationProcess("eventMetadata", eventMetadata)
    }
    s"$testName(problem)" in {
      checkSerializationDeserializationProcess("problem", problem)
    }
    s"$testName(metrics)" in {
      checkSerializationDeserializationProcess("metrics", metrics)
    }
    s"$testName(partition)" in {
      checkSerializationDeserializationProcess("partition", partition)
    }
    s"$testName(cursor)" in {
      checkSerializationDeserializationProcess("cursor", cursor)
    }
    s"$testName(eventTypeSchema)" in {
      checkSerializationDeserializationProcess("eventTypeSchema", eventTypeSchema)
    }
    s"$testName(eventValidationStrategy)" in {
      checkSerializationDeserializationProcess("eventValidationStrategy", eventValidationStrategy)
    }
    s"$testName(partitionResolutionStrategy)" in {
      checkSerializationDeserializationProcess("partitionResolutionStrategy", partitionResolutionStrategy)
    }
    s"$testName(eventEnrichmentStrategy)" in {
      checkSerializationDeserializationProcess("eventEnrichmentStrategy", partitionResolutionStrategy)
    }
    s"$testName(businessEvent)" in {
      checkSerializationDeserializationProcess("businessEvent", businessEvent)
    }
    s"$testName(dataChangeEventQualifier)" in {
      checkSerializationDeserializationProcess("dataChangeEventQualifier", dataChangeEventQualifier)
    }
    s"$testName(dataChangeEvent)" in {
      checkSerializationDeserializationProcess("dataChangeEvent", dataChangeEvent)
    }
    s"$testName(eventType)" in {
      checkSerializationDeserializationProcess("eventType", eventType)
    }
    s"$testName(event)" in {
      checkSerializationDeserializationProcess("event", event)
    }
    s"$testName(eventStreamBatch)" in {
      checkSerializationDeserializationProcess("eventStreamBatch", eventStreamBatch)
    }
    s"$testName(eventTypeStatistics)" in {
      checkSerializationDeserializationProcess("eventTypeStatistics", eventTypeStatistics)
    }
    s"$testName(batchItemResponse)" in {
      checkSerializationDeserializationProcess("batchItemResponse", batchItemResponse)
    }

  }

  def checkSerializationDeserializationProcess[T](key: String, value: T)(implicit m: Marshaller[T, String], um: Unmarshaller[String, T]) {
    val futureJsonEntity = Marshal(value).to[String] // Marshal
    val jsonEntity = Await.result(futureJsonEntity, 1.second)
    println(jsonEntity)
    val futureScalaEntity = Unmarshal(jsonEntity).to[T] //Unmarshal
    val scalaEntity = Await.result(futureScalaEntity, 1.second)
    assert(scalaEntity == value, s"Failed to marshall $key correctly!!!")
  }

}