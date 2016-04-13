package org.zalando.nakadi.client.model

import scala.reflect.runtime.universe
import org.slf4j.LoggerFactory
import org.zalando.nakadi.client.NakadiDeserializer
import org.zalando.nakadi.client.NakadiSerializer
import org.zalando.nakadi.client.model.BatchItemPublishingStatus.BatchItemPublishingStatus
import org.zalando.nakadi.client.model.BatchItemStep.BatchItemStep
import org.zalando.nakadi.client.model.DataOperation.DataOperation
import org.zalando.nakadi.client.model.EventTypeCategory.EventTypeCategory
import org.zalando.nakadi.client.model.SchemaType.SchemaType
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.typesafe.scalalogging.Logger
import scala.reflect._
import scala.reflect.runtime.universe
import scala.reflect.runtime.universe._
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer
import com.fasterxml.jackson.annotation.JsonInclude



trait JacksonJsonMarshaller {
  val logger = Logger(LoggerFactory.getLogger(this.getClass))
  val factory = new JsonFactory();


  // All TypeReferences
  implicit def problemTR: TypeReference[Problem] = new TypeReference[Problem] {}
  implicit def metricsTR: TypeReference[Metrics] = new TypeReference[Metrics] {}
  implicit def partitionTR: TypeReference[Partition] = new TypeReference[Partition] {}
  implicit def cursorTR: TypeReference[Cursor] = new TypeReference[Cursor] {}
  implicit def eventTypeSchemaTR: TypeReference[EventTypeSchema] = new TypeReference[EventTypeSchema] {}
  implicit def eventValidationStrategyTR: TypeReference[EventValidationStrategy] = new TypeReference[EventValidationStrategy] {}
  implicit def partitionResolutionStrategyTR: TypeReference[PartitionResolutionStrategy] = new TypeReference[PartitionResolutionStrategy] {}
  implicit def eventEnrichmentStrategyTR: TypeReference[EventEnrichmentStrategy] = new TypeReference[EventEnrichmentStrategy] {}
  implicit def dataChangeEventQualifierTR: TypeReference[DataChangeEventQualifier] = new TypeReference[DataChangeEventQualifier] {}
  implicit def eventTypeStatisticsTR: TypeReference[EventTypeStatistics] = new TypeReference[EventTypeStatistics] {}
  implicit def eventTypeTR: TypeReference[EventType] = new TypeReference[EventType] {}
  implicit def eventTR: TypeReference[Event] = new TypeReference[Event] {}

  implicit def eventMetadataTR: TypeReference[EventMetadata] = new TypeReference[EventMetadata] {}
  implicit def businessEventTR: TypeReference[BusinessEvent] = new TypeReference[BusinessEvent] {}
  implicit def batchItemResponseTR: TypeReference[BatchItemResponse] = new TypeReference[BatchItemResponse] {}
  implicit def dataChangeEventTR: TypeReference[DataChangeEvent[Any]] = new TypeReference[DataChangeEvent[Any]] {}

  //Lists
  implicit def listOfPartitionResolutionStrategyTR: TypeReference[Seq[PartitionResolutionStrategy]] = new TypeReference[Seq[PartitionResolutionStrategy]] {}
  implicit def listOfEventValidationStrategyTR: TypeReference[Seq[EventValidationStrategy]] = new TypeReference[Seq[EventValidationStrategy]] {}
  implicit def listOfEventEnrichmentStrategyTR: TypeReference[Seq[EventEnrichmentStrategy]] = new TypeReference[Seq[EventEnrichmentStrategy]] {}
  implicit def listOfEventTypeTR: TypeReference[Seq[EventType]] = new TypeReference[Seq[EventType]] {}

  implicit def seqDeserializer[T](implicit expectedType: TypeReference[Seq[T]]) = new NakadiDeserializer[Seq[T]] {
    def fromJson(from: String): Seq[T] = defaultObjectMapper.readValue[Seq[T]](from, expectedType)
  }
  implicit def seqSerializer[T]() = new NakadiSerializer[Seq[T]] {
    def toJson(from: Seq[T]): String = defaultObjectMapper.writeValueAsString(from)
  }

  implicit def serializer[T](implicit expectedType: TypeReference[T]): NakadiSerializer[T] = new NakadiSerializer[T] {
    def toJson(from: T): String = defaultObjectMapper.writeValueAsString(from)
  }

  implicit def deserializer[T](implicit expectedType: TypeReference[T]): NakadiDeserializer[T] = new NakadiDeserializer[T] {
    def fromJson(from: String): T = defaultObjectMapper.readValue[T](from, expectedType)
  }

  lazy val defaultObjectMapper: ObjectMapper = new ObjectMapper().registerModule(new DefaultScalaModule)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    .setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)
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
 
