package org.zalando.nakadi.client.model

import scala.reflect.runtime.universe
import scala.reflect.runtime.universe
import scala.reflect.runtime.universe._

import org.slf4j.LoggerFactory
import org.zalando.nakadi.client.Deserializer
import org.zalando.nakadi.client.Serializer

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

object JacksonJsonMarshaller {
  val logger = Logger(LoggerFactory.getLogger(this.getClass))
  val factory = new JsonFactory();

  // All TypeReferences
  implicit def problemTR: TypeReference[Problem] = new TypeReference[Problem] {}
  implicit def metricsTR: TypeReference[Metrics] = new TypeReference[Metrics] {}
  implicit def partitionTR: TypeReference[Partition] = new TypeReference[Partition] {}
  implicit def cursorTR: TypeReference[Cursor] = new TypeReference[Cursor] {}
  implicit def eventTypeSchemaTR: TypeReference[EventTypeSchema] = new TypeReference[EventTypeSchema] {}
  implicit def eventValidationStrategyTR: TypeReference[EventValidationStrategy] = new TypeReference[EventValidationStrategy] {}
  implicit def partitionResolutionStrategyTR: TypeReference[PartitionStrategy] = new TypeReference[PartitionStrategy] {}
  implicit def eventEnrichmentStrategyTR: TypeReference[EventEnrichmentStrategy] = new TypeReference[EventEnrichmentStrategy] {}
  implicit def dataChangeEventQualifierTR: TypeReference[DataChangeEventQualifier] = new TypeReference[DataChangeEventQualifier] {}
  implicit def eventTypeStatisticsTR: TypeReference[EventTypeStatistics] = new TypeReference[EventTypeStatistics] {}
  implicit def eventTypeTR: TypeReference[EventType] = new TypeReference[EventType] {}
  implicit def eventTR: TypeReference[Event] = new TypeReference[Event] {}
  implicit def eventStreamBatchTR: TypeReference[EventStreamBatch[_]] = new TypeReference[EventStreamBatch[_]] {}

  implicit def eventMetadataTR: TypeReference[EventMetadata] = new TypeReference[EventMetadata] {}
  implicit def businessEventTR: TypeReference[BusinessEvent] = new TypeReference[BusinessEvent] {}
  implicit def batchItemResponseTR: TypeReference[BatchItemResponse] = new TypeReference[BatchItemResponse] {}
  implicit def dataChangeEventTR: TypeReference[DataChangeEvent[Any]] = new TypeReference[DataChangeEvent[Any]] {}

  //Lists
  implicit def listOfPartitionStrategyTR: TypeReference[Seq[PartitionStrategy]] = new TypeReference[Seq[PartitionStrategy]] {}
  implicit def listOfEventValidationStrategyTR: TypeReference[Seq[EventValidationStrategy]] = new TypeReference[Seq[EventValidationStrategy]] {}
  implicit def listOfEventEnrichmentStrategyTR: TypeReference[Seq[EventEnrichmentStrategy]] = new TypeReference[Seq[EventEnrichmentStrategy]] {}
  implicit def listOfEventTypeTR: TypeReference[Seq[EventType]] = new TypeReference[Seq[EventType]] {}
  implicit def listOfPartitionTR: TypeReference[Seq[Partition]] = new TypeReference[Seq[Partition]] {}

  implicit def optionalDeserializer[T](implicit expectedType: TypeReference[T]): Deserializer[Option[T]] = new Deserializer[Option[T]] {
    def from(from: String): Option[T] = {

      defaultObjectMapper.readValue[Option[T]](from, expectedType)
    }
  }

  implicit def serializer[T]: Serializer[T] = new Serializer[T] {
    def to(from: T): String = defaultObjectMapper.writeValueAsString(from)
  }

  implicit def deserializer[T](implicit expectedType: TypeReference[T]): Deserializer[T] = new Deserializer[T] {
    def from(from: String): T = defaultObjectMapper.readValue[T](from, expectedType)
  }

  lazy val defaultObjectMapper: ObjectMapper = new ObjectMapper() //
    .registerModule(new DefaultScalaModule)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    .setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)
    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
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
 
