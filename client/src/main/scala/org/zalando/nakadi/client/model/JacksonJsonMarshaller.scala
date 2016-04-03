package org.zalando.nakadi.client.model

import scala.annotation.implicitNotFound
import scala.concurrent.{ ExecutionContext, Future }
import scala.reflect.Manifest
import org.slf4j.LoggerFactory
import com.fasterxml.jackson.core.{ JsonFactory, JsonParser }
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.{ DeserializationContext, DeserializationFeature, JsonDeserializer, ObjectMapper, PropertyNamingStrategy, SerializationFeature }
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.typesafe.scalalogging.Logger
import akka.http.scaladsl.marshalling.Marshaller
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.stream.ActorMaterializer
import javax.annotation.PostConstruct
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
trait JacksonJsonMarshaller {
  import JacksonJsonMarshaller._
  val factory = new JsonFactory();

  class DataOperationType extends TypeReference[DataOperation.type]
  class EventTypeCategoryType extends TypeReference[EventTypeCategory.type]
  class BatchItemStepType extends TypeReference[BatchItemStep.type]
  class BatchItemPublishingStatusType extends TypeReference[BatchItemPublishingStatus.type]

  implicit val DataOperationTypeTypeReference = new TypeReference[DataOperationType] {}
  implicit val EventTypeCategoryTypeTypeReference = new TypeReference[EventTypeCategoryType] {}
  implicit val BatchItemStepTypeTypeReference = new TypeReference[BatchItemStepType] {}
  implicit val BatchItemPublishingStatusTypeTypeReference = new TypeReference[BatchItemPublishingStatusType] {}

  implicit val problemTypeReference = new TypeReference[Problem] {}
  implicit val metricsTypeReference = new TypeReference[Metrics] {}
  implicit val partitionTypeReference = new TypeReference[Partition] {}
  implicit val cursorTypeReference = new TypeReference[Cursor] {}
  implicit val eventTypeSchemaTypeReference = new TypeReference[EventTypeSchema] {}
  implicit val eventValidationStrategyTypeReference = new TypeReference[EventValidationStrategy] {}
  implicit val partitionResolutionStrategyTypeReference = new TypeReference[PartitionResolutionStrategy] {}
  implicit val eventEnrichmentStrategyTypeReference = new TypeReference[EventEnrichmentStrategy] {}
  implicit val dataChangeEventQualifierTypeReference = new TypeReference[DataChangeEventQualifier] {}
  implicit val eventTypeStatisticsTypeReference = new TypeReference[EventTypeStatistics] {}
  implicit val eventTypeTypeReference = new TypeReference[EventType] {}
  implicit val eventTypeReference = new TypeReference[Event] {}
  implicit val eventStreamBatchTypeReference = new TypeReference[EventStreamBatch] {}
  implicit val eventMetadataTypeReference = new TypeReference[EventMetadata] {}
  implicit val businessEventTypeReference = new TypeReference[BusinessEvent] {}
  implicit val batchItemResponseTypeReference = new TypeReference[BatchItemResponse] {}

  implicit def dataChangeEventTypeReference[T](implicit expectedType: TypeReference[T]) = { new TypeReference[DataChangeEvent[T]] {} }

  implicit def unmarshaller[T: Manifest](implicit ec: ExecutionContext, am: ActorMaterializer, expectedType: TypeReference[T]): Unmarshaller[HttpEntity, Future[T]] = Unmarshaller.strict {
    input => Unmarshaller.byteArrayUnmarshaller(input).map(objectMapper.readValue(_, expectedType))
  }

  implicit def objectToStringMarshaller[T]: Marshaller[T, String] = Marshaller.opaque {
    input => objectMapper.writeValueAsString(input)
  }

  implicit def stringToObjectUnmarshaller[T](implicit expectedType: TypeReference[T]): Unmarshaller[String, T] = Unmarshaller.strict {
    input => objectMapper.readValue[T](input, expectedType)
  }

}

private[model] object JacksonJsonMarshaller {
  val logger = Logger(LoggerFactory.getLogger(JacksonJsonMarshaller.getClass))
  def objectMapper(): ObjectMapper = {
    val m = new ObjectMapper {
      @PostConstruct
      def customConfiguration() {
        // Uses Enum.toString() for serialization of an Enum
        this.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        // Uses Enum.toString() for deserialization of an Enum
        this.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
      }

    }
    m.registerModule(new DefaultScalaModule)
    m.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    m.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    m.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)
    m.addHandler(new DeserializationProblemHandler() {
      override def handleUnknownProperty(ctxt: DeserializationContext,
                                         jp: JsonParser, deserializer: JsonDeserializer[_],
                                         beanOrClass: AnyRef,
                                         propertyName: String): Boolean = {
        logger.warn(s"unknown property occurred in JSON representation: [beanOrClass=$beanOrClass, property=$propertyName]")
        true
      }
    })
    m
  }

  def enumSerizalizer[T <: Enumeration]() = new JsonSerializer[T#Value] {
    override def serialize(value: T#Value, jgen: JsonGenerator, provider: SerializerProvider): Unit = {
      jgen.writeString(value.toString())
    }
  }

}