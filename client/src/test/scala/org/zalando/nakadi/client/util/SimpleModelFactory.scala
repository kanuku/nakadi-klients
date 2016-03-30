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

object SimpleModelFactory {

  //
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
  import spray.json.DefaultJsonProtocol._

  //Simple bjects composed out of scalar typers only (without dependency to other Object-models)
  val eventMetadata = new EventMetadata("eid", "eventType", "occurredAt", "receivedAt", List("parentEids"), "flowId", Map("key" -> "value"))
  val problem = new Problem("problemType", "title", 312, "detail", "instance")
  val metrics = new Metrics("metrics")
  val partition = new Partition("partition", "oldestAvailableOffset", "newestAvailableOffset")
  val cursor = new Cursor("partition", "offset")
  val eventTypeSchema = new EventTypeSchema("schemaType", "schema")
  val eventValidationStrategy = new EventValidationStrategy("name", "doc")
  val partitionResolutionStrategy = new PartitionResolutionStrategy("name", "doc")
  val eventEnrichmentStrategy = new EventEnrichmentStrategy("name", "doc")

  //Complex objects
  val businessEvent = new BusinessEvent(eventMetadata)
  val dataChangeEventQualifier = new DataChangeEventQualifier("dataType", DataOperation.CREATE)
  val dataChangeEvent = new DataChangeEvent(metrics, dataChangeEventQualifier, eventMetadata)

  val eventType = new EventType("name", "owner", "category", "effectiveSchema", List("validationStrategies"), List("enrichmentStrategies"), partitionResolutionStrategy, eventTypeSchema, List("dataKeyFields"),
    List("partitioningKeyFields"))
  val event = new Event(eventType, true, "title")
  val eventStreamBatch = new EventStreamBatch(cursor, List(event, event))

}