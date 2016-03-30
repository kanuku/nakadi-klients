package org.zalando.nakadi.client.model

import org.zalando.nakadi.client.utils.ParseHelper

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.Marshaller
import akka.http.scaladsl.unmarshalling.Unmarshaller
import spray.json.{ DefaultJsonProtocol, JsonFormat, JsonReader, JsonWriter, pimpAny, pimpString }

trait DefaultMarshaller extends SprayJsonSupport with DefaultJsonProtocol {
  import org.zalando.nakadi.client.utils.ParseHelper._

  implicit val eventMetadataFormatter = jsonFormat7(EventMetadata)
  implicit val problemFormatter = jsonFormat5(Problem)
  implicit val metricsFormatter = jsonFormat1(Metrics)
  implicit val partitionFormatter = jsonFormat3(Partition)
  implicit val cursorFormatter = jsonFormat2(Cursor)
  implicit val dataChangeEventQualifierFormatter = jsonFormat2(DataChangeEventQualifier)
  implicit val partitionResolutionStrategyFormatter = jsonFormat2(PartitionResolutionStrategy)
  implicit val businessEventFormatter = jsonFormat1(BusinessEvent)
  implicit val eventTypeSchemaFormatter = jsonFormat2(EventTypeSchema)
  implicit val eventValidationStrategyFormatter = jsonFormat2(EventValidationStrategy)
  implicit val eventEnrichmentStrategyFormatter = jsonFormat2(EventEnrichmentStrategy)
  implicit val eventTypeFormatter = jsonFormat10(EventType)
  implicit val eventFormatter = jsonFormat3(Event)
  implicit val eventStreamBatchFormatter = jsonFormat2(EventStreamBatch)

  implicit def dataChangeEventFormatter[A: JsonFormat] = jsonFormat3(DataChangeEvent.apply[A])

  //Handy marshallers
  implicit def toStringMarshaller[T](implicit writer: JsonWriter[T]): Marshaller[T, String] =
    Marshaller.opaque {
      _.toJson(writer).toString()
    }

  implicit def fromEntityUnmarshaller[T](implicit reader: JsonReader[T]): Unmarshaller[String, T] = Unmarshaller.strict {
    input => input.parseJson.convertTo[T]
  }

}