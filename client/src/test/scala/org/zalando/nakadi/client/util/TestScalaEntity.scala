package org.zalando.nakadi.client.util

import org.zalando.nakadi.client.model._
import org.zalando.nakadi.client.model.Problem
import org.zalando.nakadi.client.model.PartitionResolutionStrategy
import org.zalando.nakadi.client.model.Partition
import org.zalando.nakadi.client.model.Metrics
import org.zalando.nakadi.client.model.EventValidationStrategy
import org.zalando.nakadi.client.model.EventTypeSchema
import org.zalando.nakadi.client.model.EventType
import org.zalando.nakadi.client.model.EventStreamBatch
import org.zalando.nakadi.client.model.EventMetadata
import org.zalando.nakadi.client.model.EventEnrichmentStrategy
import org.zalando.nakadi.client.model.Event
import org.zalando.nakadi.client.model.DataOperation
import org.zalando.nakadi.client.model.DataChangeEventQualifier
import org.zalando.nakadi.client.model.DataChangeEvent
import org.zalando.nakadi.client.model.Cursor
import org.zalando.nakadi.client.model.BusinessEvent

object TestScalaEntity {

  //
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
  import spray.json.DefaultJsonProtocol._

  //Simple bjects composed out of scalar typers only (without dependency to other Object-models)
  val problem = new Problem("problemType", "title", 312, Option("detail"), Option("instance"))
  val metrics = new Metrics("metrics")
  val partition = new Partition("partition", "oldestAvailableOffset", "newestAvailableOffset")
  val cursor = new Cursor("partition", "offset")
  val eventTypeSchema = new EventTypeSchema(SchemaType.JSON, "schema")
  val eventValidationStrategy = new EventValidationStrategy("name", Option("doc"))
  val partitionResolutionStrategy = new PartitionResolutionStrategy("name", Option("doc"))
  val eventEnrichmentStrategy = new EventEnrichmentStrategy("name", Option("doc"))

  //Complex objects
  val dataChangeEventQualifier = new DataChangeEventQualifier("dataType", DataOperation.CREATE)

  val eventTypeStatistics = new EventTypeStatistics(Option(9281002), Option(19283), Option(21), Option(312))
  val eventType = new EventType("name", "owner", EventTypeCategory.BUSINESS, Option(List("validationStrategies")), Option(List("enrichmentStrategies")), partitionResolutionStrategy, Option(eventTypeSchema), Option(List("dataKeyFields")), Option(List("partitioningKeyFields")), Option(eventTypeStatistics))
  val event = new Event(eventType, true, "title")
  val eventStreamBatch = new EventStreamBatch(cursor, List(event, event))
  val eventMetadata = new EventMetadata("eid", Option(eventType), "occurredAt", Option("receivedAt"), List("parentEids"), Option("flowId"), Option("partition"), Map("key" -> "value"))
  val dataChangeEvent = new DataChangeEvent(metrics, dataChangeEventQualifier, eventMetadata)
  val businessEvent = new BusinessEvent(eventMetadata)
  val batchItemResponse = new BatchItemResponse(Option("eid"), BatchItemPublishingStatus.SUBMITTED, Option(BatchItemStep.PUBLISHING), Option("detail"))
}