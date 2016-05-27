package org.zalando.nakadi.client.java.model

import org.slf4j.LoggerFactory
import org.zalando.nakadi.client.Deserializer
import org.zalando.nakadi.client.Serializer
import org.zalando.nakadi.client.java.enumerator._

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.typesafe.scalalogging.Logger
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion

object JavaJacksonJsonMarshaller {
  val logger = Logger(LoggerFactory.getLogger(this.getClass))
  // All TypeReferences
   def problemTR: TypeReference[Problem] = new TypeReference[Problem] {}
   def metricsTR: TypeReference[Metrics] = new TypeReference[Metrics] {}
   def partitionTR: TypeReference[Partition] = new TypeReference[Partition] {}
   def cursorTR: TypeReference[Cursor] = new TypeReference[Cursor] {}
   def eventTypeSchemaTR: TypeReference[EventTypeSchema] = new TypeReference[EventTypeSchema] {}
   def eventValidationStrategyTR: TypeReference[EventValidationStrategy] = new TypeReference[EventValidationStrategy] {}
   def partitionResolutionStrategyTR: TypeReference[PartitionStrategy] = new TypeReference[PartitionStrategy] {}
   def eventEnrichmentStrategyTR: TypeReference[EventEnrichmentStrategy] = new TypeReference[EventEnrichmentStrategy] {}
   def dataChangeEventQualifierTR: TypeReference[DataChangeEventQualifier] = new TypeReference[DataChangeEventQualifier] {}
   def eventTypeStatisticsTR: TypeReference[EventTypeStatistics] = new TypeReference[EventTypeStatistics] {}
   def eventTypeTR: TypeReference[EventType] = new TypeReference[EventType] {}
   def eventTR: TypeReference[Event] = new TypeReference[Event] {}
   def eventStreamBatchTR: TypeReference[EventStreamBatch[_]] = new TypeReference[EventStreamBatch[_]] {}

   def eventMetadataTR: TypeReference[EventMetadata] = new TypeReference[EventMetadata] {}
   def businessEventTR: TypeReference[BusinessEvent] = new TypeReference[BusinessEvent] {}
   def batchItemResponseTR: TypeReference[BatchItemResponse] = new TypeReference[BatchItemResponse] {}
   def dataChangeEventTR: TypeReference[DataChangeEvent[Any]] = new TypeReference[DataChangeEvent[Any]] {}

  //Lists
   def listOfPartitionStrategyTR: TypeReference[java.util.List[PartitionStrategy]] = new TypeReference[java.util.List[PartitionStrategy]] {}
   def listOfEventValidationStrategyTR: TypeReference[java.util.List[EventValidationStrategy]] = new TypeReference[java.util.List[EventValidationStrategy]] {}
   def listOfEventEnrichmentStrategyTR: TypeReference[java.util.List[EventEnrichmentStrategy]] = new TypeReference[java.util.List[EventEnrichmentStrategy]] {}
   def listOfEventTypeTR: TypeReference[java.util.List[EventType]] = new TypeReference[java.util.List[EventType]] {}
   def listOfPartitionTR: TypeReference[java.util.List[Partition]] = new TypeReference[java.util.List[Partition]] {}

   def optionalDeserializer[T]( expectedType: TypeReference[T]): Deserializer[Option[T]] = new Deserializer[Option[T]] {
    def from(from: String): Option[T] = {
      defaultObjectMapper.readValue[Option[T]](from, expectedType)
    }
  }

   def serializer[T]: Serializer[T] = new Serializer[T] {
    def to(from: T): String = defaultObjectMapper.writeValueAsString(from)
  }

   def deserializer[T]( expectedType: TypeReference[T]): Deserializer[T] = new Deserializer[T] {
    def from(from: String): T = {
      defaultObjectMapper.readValue[T](from, expectedType)
    }
  }

  lazy val defaultObjectMapper: ObjectMapper = new ObjectMapper() //
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
    .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
    .addHandler(new DeserializationProblemHandler() {
      override def handleUnknownProperty(ctxt: DeserializationContext,
                                         jp: JsonParser, deserializer: JsonDeserializer[_],
                                         beanOrClass: AnyRef,
                                         propertyName: String): Boolean = {
        logger.warn(s"unknown property occurred in JSON representation: [beanOrClass=$beanOrClass, property=$propertyName]")
        true
      }
    })
}