package org.zalando.nakadi.client.scala

import scala.concurrent.duration.DurationInt
import org.scalatest.Matchers
import org.scalatest.WordSpec
import org.zalando.nakadi.client.scala.model._
import org.zalando.nakadi.client.scala.model.JacksonJsonMarshaller
import org.zalando.nakadi.client.utils.AkkaConfig
import org.zalando.nakadi.client.utils.TestScalaEntity
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import org.zalando.nakadi.client.Deserializer
import org.zalando.nakadi.client.Serializer

/**
 * Tests the Marshalling and Umarshalling of the same object in a single run. It tests in this sequence: 1.Marshall and 2.Unmarshall. <br>
 * This is just a simple test that should break when Custom
 * Marshallers/Unmarshallers are used and produce different
 * unexpected results.
 */
class SerializerDeserializerTest extends WordSpec with Matchers with AkkaConfig {
import JacksonJsonMarshaller._
  import TestScalaEntity._

  "When an entity(scala object) is marshalled and unmarshalled it" should {
    val testName = "always result in the same entity"
    s"$testName(eventMetadata)" in {
      checkSerializationDeserializationProcess("eventMetadata", eventMetadata)
 
    }
    "EventType" in {
      println(" ####### " + EventTypeCategory.withName("business"))
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
    //    s"$testName(dataChangeEvent)" in {
    //      checkSerializationDeserializationProcess("dataChangeEvent", dataChangeEvent)
    //    }
    s"$testName(eventType)" in {
      checkSerializationDeserializationProcess("eventType", eventType)
    }
    //    s"$testName(event)" in {
    //      checkSerializationDeserializationProcess("event", event) 
    //    }
    s"$testName(eventStreamBatch)" in {
    	  implicit val myEventStreamBatchTR: TypeReference[EventStreamBatch[MyEvent]] = new TypeReference[EventStreamBatch[MyEvent]] {}
      checkSerializationDeserializationProcess("eventStreamBatch", eventStreamBatch)
    }
    s"$testName(eventTypeStatistics)" in {
      checkSerializationDeserializationProcess("eventTypeStatistics", eventTypeStatistics)
    }
    s"$testName(batchItemResponse)" in {
      checkSerializationDeserializationProcess("batchItemResponse", batchItemResponse)
    }

  }

  def checkSerializationDeserializationProcess[T](key: String, value: T)(implicit ser: Serializer[T], des: Deserializer[T]) {
    val jsonEntity = ser.to(value) // Marshal
    println("#### Json-Entity:" + jsonEntity)
    val scalaEntity = des.from(jsonEntity) //Unmarshal
    println("#### Scala-Entity:" + scalaEntity)
    assert(scalaEntity == value, s"Failed to marshall $key correctly!!!")
  }

}