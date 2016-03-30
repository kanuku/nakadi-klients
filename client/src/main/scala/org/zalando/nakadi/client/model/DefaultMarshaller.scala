package org.zalando.nakadi.client.model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import org.zalando.nakadi.client.utils.ParseHelper

trait DefaultMarshaller extends SprayJsonSupport with DefaultJsonProtocol {
  import ParseHelper._

  implicit val eventMetadata = jsonFormat7(EventMetadata)
  implicit val problem = jsonFormat5(Problem)
  implicit val metrics = jsonFormat1(Metrics)
  implicit val partition = jsonFormat3(Partition)
  implicit val cursor = jsonFormat2(Cursor)
  implicit val dataChangeEventQualifier = jsonFormat2(DataChangeEventQualifier)
  implicit val partitionResolutionStrategy = jsonFormat2(PartitionResolutionStrategy)
  implicit val businessEvent = jsonFormat1(BusinessEvent)
  implicit val eventTypeSchema = jsonFormat2(EventTypeSchema)
  implicit val eventValidationStrategy = jsonFormat2(EventValidationStrategy)
  implicit val eventEnrichmentStrategy = jsonFormat2(EventEnrichmentStrategy)
  implicit val eventType = jsonFormat10(EventType)
  implicit val event = jsonFormat3(Event)
  implicit val eventStreamBatch = jsonFormat2(EventStreamBatch)

}
  
 