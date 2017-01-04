package org.zalando.nakadi.client.scala.model

import org.scalatest.Matchers
import org.scalatest.WordSpec
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.DeserializationFeature
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.fasterxml.jackson.core.`type`.TypeReference

class ScalaJacksonJsonMarshallerTest extends WordSpec with Matchers {
  private val log = LoggerFactory.getLogger(this.getClass());

  val mapper = ScalaJacksonJsonMarshaller.defaultObjectMapper

  private def toJson[T](in: T): String = {
    log.info("ToJson - in {}", in.toString());
    val result = ScalaJacksonJsonMarshaller.serializer.to(in);
    log.info("ToJson - out {}", result);
    result
  }

  private def toObject[T](json: String, expectedType: TypeReference[T]): T = {
    log.info("toObject - in {}", json);
    val result = ScalaJacksonJsonMarshaller.deserializer(expectedType).from(json);
    log.info("toObject - out {}", result.toString());
    result
  }

  private def testMarshallingUnmarshalling[T](in: T, expectedType: TypeReference[T]): Unit = {
    val json = toJson(in)
    val out = toObject(json, expectedType)
    in shouldBe out
  }

  "Serialize/Deserialize BatchItemResponse" in {
    val in = ModelFactory.newBatchItemResponse
    val expectedType = ScalaJacksonJsonMarshaller.batchItemResponseTR
    testMarshallingUnmarshalling(in, expectedType)
  }
  "Serialize/Deserialize BusinessEvent" in {
    val in = ModelFactory.newBusinessEvent
    val expectedType = new TypeReference[BusinessEventImpl] {}
    testMarshallingUnmarshalling(in, expectedType)

  }
  "Serialize/Deserialize Cursor" in {
    val in = ModelFactory.newCursor
    val expectedType = ScalaJacksonJsonMarshaller.cursorTR
    testMarshallingUnmarshalling(in, expectedType)

  }
  "Serialize/Deserialize DataChangeEvent" in {
    val event = ModelFactory.newSimpleEvent
    val in = ModelFactory.newDataChangeEvent(event)
    val expectedType = new TypeReference[DataChangeEvent[SimpleEvent]] {}
    testMarshallingUnmarshalling(in, expectedType)

  }
  "Serialize/Deserialize EventMetadata" in {
    val in = ModelFactory.newEventMetadata()
    val expectedType = ScalaJacksonJsonMarshaller.eventMetadataTR
    testMarshallingUnmarshalling(in, expectedType)

  }
  "Serialize/Deserialize EventStreamBatch" in {
    val events = List(ModelFactory.newSimpleEvent(), ModelFactory.newSimpleEvent(), ModelFactory.newSimpleEvent())
    val cursor = ModelFactory.newCursor()
    val in = ModelFactory.newEventStreamBatch(events, cursor)
    val expectedType = new TypeReference[EventStreamBatch[SimpleEvent]] {}
    testMarshallingUnmarshalling(in, expectedType)

  }
  "Serialize/Deserialize EventTypeSchema" in {
    val in = ModelFactory.newEventTypeSchema()
    val expectedType = ScalaJacksonJsonMarshaller.eventTypeSchemaTR
    testMarshallingUnmarshalling(in, expectedType)
  }
  "Serialize/Deserialize EventTypeStatistics" in {
    val in = ModelFactory.newEventTypeStatistics()
    val expectedType = ScalaJacksonJsonMarshaller.eventTypeStatisticsTR
    testMarshallingUnmarshalling(in, expectedType)
  }
  "Serialize/Deserialize Metrics" in {
    val in = ModelFactory.newMetrics()
    val expectedType = ScalaJacksonJsonMarshaller.metricsTR
    val json = toJson(in)
    val out = toObject(json, expectedType)
    val jsonOut = toJson(out)
    json shouldBe jsonOut
  }
  "Serialize/Deserialize Partition" in {
    val in = ModelFactory.newPartition()
    val expectedType = ScalaJacksonJsonMarshaller.partitionTR
    testMarshallingUnmarshalling(in, expectedType)
  }
  "Serialize/Deserialize Problem" in {
    val in = ModelFactory.newProblem()
    val expectedType = ScalaJacksonJsonMarshaller.problemTR
    testMarshallingUnmarshalling(in, expectedType)
  }
  "Serialize/Deserialize PartitionStrategy" in {
    val in = ModelFactory.newPartitionStrategy()
    val expectedType = ScalaJacksonJsonMarshaller.partitionResolutionStrategyTR
    testMarshallingUnmarshalling(in, expectedType)
  }
  "Serialize/Deserialize EventEnrichmentStrategy" in {
    val in = ModelFactory.newEventEnrichmentStrategy()
    val expectedType = ScalaJacksonJsonMarshaller.eventEnrichmentStrategyTR
    testMarshallingUnmarshalling(in, expectedType)
  }
  "Serialize/Deserialize EventTypeCategory" in {
    val in = ModelFactory.newEventTypeCategory()
    val expectedType = ScalaJacksonJsonMarshaller.eventTypeCategoryTR
    testMarshallingUnmarshalling(in, expectedType)
  }
  "Serialize/Deserialize EventType" in {
    val in = ModelFactory.newEventType()
    val expectedType = ScalaJacksonJsonMarshaller.eventTypeTR
    testMarshallingUnmarshalling(in, expectedType)
  }
  "Serialize/Deserialize CompatibilityMode" in {
    val in = ModelFactory.newCompatibilityMode()
    val expectedType = ScalaJacksonJsonMarshaller.compatibilityModeTR
    testMarshallingUnmarshalling(in, expectedType)
  }

}