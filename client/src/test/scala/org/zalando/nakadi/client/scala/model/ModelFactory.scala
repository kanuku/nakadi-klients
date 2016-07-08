package org.zalando.nakadi.client.scala.model

import java.util.UUID
import com.fasterxml.jackson.annotation.JsonProperty
import scala.util.Random

case class BusinessEventImpl(metadata: Option[EventMetadata]) extends BusinessEvent
case class SimpleEvent(id: String, name: String) extends Event

object ModelFactory {
  def randomUUID(): String = UUID.randomUUID().toString();
  def randomInt(): Integer = Random.nextInt()
  def randomListOfStrings(nrOfItems: Int): Seq[String] = for (i <- 1 to nrOfItems) yield randomUUID()
  def randomMapOfStrings(nrOfItems: Int): Map[String, Any] = (for (i <- 1 to nrOfItems) yield (s"itemNr $i" -> randomMapOfStrings(nrOfItems - 1))).toMap

  def newBatchItemResponse(): BatchItemResponse = {
    val detail = Option("detail")
    val step = Option(BatchItemStep.ENRICHING)
    val publishingStatus = BatchItemPublishingStatus.SUBMITTED
    val eid = Option(randomUUID())
    new BatchItemResponse(eid, publishingStatus, step, detail)
  }
  def newBusinessEvent() = new BusinessEventImpl(Option(newEventMetadata))
  def newEventMetadata(): EventMetadata = new EventMetadata(randomUUID(), Option("eventType"), "occurredAt", Option("receivedAt"), List("parentEids"), Option(randomUUID()), Option("partition"))
  def newCursor(): Cursor = new Cursor("partition", "offset")
  def newSimpleEvent(): SimpleEvent = SimpleEvent(randomUUID(), randomUUID())
  def newDataChangeEvent[T <: Event](in: T): DataChangeEvent[T] = DataChangeEvent(in, randomUUID(), DataOperation.CREATE, Option(newEventMetadata()))
  def newEventStreamBatch[T <: Event](events: List[T], cursor: Cursor): EventStreamBatch[T] = EventStreamBatch(cursor, Option(events))
  def newEventTypeSchema(): EventTypeSchema = {
    val schema = "{'propeties':{'key':'value'}}".replaceAll("'", "\"");
    EventTypeSchema(SchemaType.JSON, schema);
  }
  def newEventTypeStatistics(): EventTypeStatistics = EventTypeStatistics(randomInt(), randomInt(), randomInt(), randomInt())
  def newMetrics(): Metrics = {
    val depth = 5
    val gauges: Map[String, Any] = Map[String, Any](
      "normalString" -> randomUUID(),
      "SimpleEvent" -> newSimpleEvent(),
      "List" -> randomListOfStrings(depth),
      "MapOfMaps" -> randomMapOfStrings(depth))
    Metrics(randomUUID(), gauges)
  }
  def newPartition(): Partition = Partition(randomUUID(), randomUUID(), randomUUID())
  def newProblem(): Problem = Problem(randomUUID(), randomUUID(), randomInt(), Option(randomUUID()), Option(randomUUID()))

  def newPartitionStrategy(): PartitionStrategy.Value = {
    val values = PartitionStrategy.values.toArray
    values(Random.nextInt(values.length))
  }

  def newEventEnrichmentStrategy(): EventEnrichmentStrategy.Value = {
    val values = EventEnrichmentStrategy.values.toArray
    values(Random.nextInt(values.length))
  }

  def newEventTypeCategory(): EventTypeCategory.Value = {
    val values = EventTypeCategory.values.toArray
    values(Random.nextInt(values.length))
  }

  def newEventType(): EventType = new EventType(randomUUID(), //name
    randomUUID(), //owningApplication
    newEventTypeCategory(), //category
    List(newEventEnrichmentStrategy(), newEventEnrichmentStrategy()), //enrichmentStrategies
    Option(newPartitionStrategy()), //
    newEventTypeSchema(), //
    List(randomUUID(),
      randomUUID()), //dataKeyFields
    List(randomUUID(),
      randomUUID()), //dataKeyFields
    Option(newEventTypeStatistics))
}