package org.zalando.nakadi.client.utils

import org.zalando.nakadi.client.scala.model.BatchItemPublishingStatus
import org.zalando.nakadi.client.scala.model.BatchItemResponse
import org.zalando.nakadi.client.scala.model.BatchItemStep
import org.zalando.nakadi.client.scala.model.Cursor
import org.zalando.nakadi.client.scala.model.DataChangeEvent
import org.zalando.nakadi.client.scala.model.DataOperation
import org.zalando.nakadi.client.scala.model.Event
import org.zalando.nakadi.client.scala.model.EventEnrichmentStrategy
import org.zalando.nakadi.client.scala.model.EventMetadata
import org.zalando.nakadi.client.scala.model.EventStreamBatch
import org.zalando.nakadi.client.scala.model.EventType
import org.zalando.nakadi.client.scala.model.EventTypeCategory
import org.zalando.nakadi.client.scala.model.EventTypeSchema
import org.zalando.nakadi.client.scala.model.EventTypeStatistics
import org.zalando.nakadi.client.scala.model.Metrics
import org.zalando.nakadi.client.scala.model.Partition
import org.zalando.nakadi.client.scala.model.PartitionStrategy
import org.zalando.nakadi.client.scala.model.Problem
import org.zalando.nakadi.client.scala.model.SchemaType

object TestScalaEntity {
  //
  //  Simple bjects composed out of scalar typers only (without dependency to other Object-models)
  val problem = Problem("problemType", "title", 312, Option("detail"), Option("instance"))
  val metrics = Metrics("2.0.1",Map("metrics" -> "test"))
  val partition = Partition("0", "132", "4423")
  val cursor = Cursor("0", "120")
  val eventTypeSchema = EventTypeSchema(SchemaType.JSON, "schema")
  val partitionResolutionStrategy = PartitionStrategy.HASH
  val eventEnrichmentStrategy = EventEnrichmentStrategy.METADATA

  //Complex objects
  val eventTypeStatistics = EventTypeStatistics(9281002, 19283, 21, 312)
  val eventType = EventType("name", "owner", 
      EventTypeCategory.BUSINESS 
      , List(EventEnrichmentStrategy.METADATA), Some(partitionResolutionStrategy), 
      eventTypeSchema, 
      List("dataKeyFields"), List("partitioningKeyFields"), Option(eventTypeStatistics))
  val eventMetadata = EventMetadata("eid", Option(eventType.name), "occurredAt", Option("receivedAt"), List("parentEids"), Option("flowId"), Option("partition"))
  case class MyEvent(name: String, metadata: Option[EventMetadata]) extends Event
  val myEvent = MyEvent("test", Some(eventMetadata))
  val eventStreamBatch = EventStreamBatch[MyEvent](cursor, Option(List(myEvent)))
  val batchItemResponse = BatchItemResponse(Option("eid"), BatchItemPublishingStatus.SUBMITTED, Option(BatchItemStep.PUBLISHING), Option("detail"))

  // custom event
  case class CommissionEntity(id: String, ql: List[String])
  val commissionEntity = CommissionEntity("id2", List("ql1", "ql2"))
  val dataChangeEvent = DataChangeEvent[CommissionEntity](commissionEntity, "Critical", DataOperation.DELETE, Some(eventMetadata))
}