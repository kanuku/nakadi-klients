package org.zalando.nakadi.client.model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.Marshaller
import akka.http.scaladsl.unmarshalling.Unmarshaller
import spray.json.{ DefaultJsonProtocol, JsValue, JsonFormat, JsonReader, JsonWriter, pimpAny, pimpString }
import spray.json._
import org.zalando.nakadi.client.utils.ParseHelper

trait SprayJsonMarshaller extends SprayJsonSupport with DefaultJsonProtocol {
  import ParseHelper._
  case class Test(subjectDescription: String)
  implicit val testFormatter = jsonFormat1(Test)
  //Enums
  implicit val dataOperationEnumFormatter = jsonEnumFormat(DataOperation)
  implicit val eventTypeCategoryEnumFormatter = jsonEnumFormat(EventTypeCategory)
  implicit val batchItemStepjsonEnumFormat = jsonEnumFormat(BatchItemStep)
  implicit val BatchItemPublishingStatusEnumFormat = jsonEnumFormat(BatchItemPublishingStatus)

  implicit val problemFormatter = jsonFormat(Problem, "problem_type", "title", "status", "detail", "instance")
  implicit val metricsFormatter = jsonFormat1(Metrics)
  implicit val partitionFormatter = jsonFormat(Partition, "partition", "oldest_available_offset", "newest_available_offset")
  implicit val cursorFormatter = jsonFormat2(Cursor)
  implicit val dataChangeEventQualifierFormatter = jsonFormat(DataChangeEventQualifier, "data_type", "data_operation")
  implicit val partitionResolutionStrategyFormatter = jsonFormat2(PartitionResolutionStrategy)
  implicit val eventTypeSchemaFormatter = jsonFormat(EventTypeSchema, "schema_type", "schema")
  implicit val eventTypeStatisticsFormatter = jsonFormat(EventTypeStatistics, "expected_write_rate", "message_size", "read_parallelism", "write_parallelism")
  implicit val eventValidationStrategyFormatter = jsonFormat2(EventValidationStrategy)
  implicit val eventTypeFormatter = jsonFormat(EventType, "name", "owning_application", "category", "validation_strategies", "enrichment_strategies", "partition_resolution_strategy", "schema", "data_key_fields", "partitioning_key_fields", "statistics")
  implicit val eventMetadataFormatter = jsonFormat(EventMetadata, "eid", "event_type", "occurred_at", "received_at", "parent_eids", "flow_id", "partition", "metadata")
  implicit val businessEventFormatter = jsonFormat1(BusinessEvent)
  implicit val eventEnrichmentStrategyFormatter = jsonFormat2(EventEnrichmentStrategy)
  implicit val eventFormatter = jsonFormat(Event,"event_type","additional_properties","title")
  implicit val eventStreamBatchFormatter = jsonFormat2(EventStreamBatch)
  implicit val batchItemResponseFormatter = jsonFormat(BatchItemResponse,"eid","publishing_status","step","detail")

  implicit def dataChangeEventFormatter[A: JsonFormat] = jsonFormat3(DataChangeEvent.apply[A])

  //Custom Marshaller/Unmarshaller 
  implicit def entityToStringMarshaller[T](implicit writer: JsonWriter[T]): Marshaller[T, String] = Marshaller.opaque {
    _.toJson(writer).toString()
  }

  implicit def stringToEntityUnmarshaller[T](implicit reader: JsonReader[T]): Unmarshaller[String, T] = Unmarshaller.strict {
    input => input.parseJson.convertTo[T]
  }

  implicit def entityToJsValueMarshaller[T](implicit writer: JsonWriter[T]): Marshaller[T, JsValue] = Marshaller.opaque {
    _.toJson(writer)
  }

  implicit def jsValueToEntityUnmarshaller[T](implicit reader: JsonReader[T]): Unmarshaller[JsValue, T] = Unmarshaller.strict {
    input => input.convertTo[T]
  }

}