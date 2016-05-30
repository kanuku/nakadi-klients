package org.zalando.nakadi.client.scala

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.Random
import org.zalando.nakadi.client.scala.model._
import org.zalando.nakadi.client.scala.model.JacksonJsonMarshaller
import org.zalando.nakadi.client.Serializer
import org.zalando.nakadi.client.scala.model.JacksonJsonMarshaller
import org.zalando.nakadi.client.Deserializer
import org.zalando.nakadi.client.Deserializer
import org.zalando.nakadi.client.Serializer
import java.util.Calendar


case class EventActions(client: Client) {
  def create[T <: Event](name: String, event: Seq[T])  = {
    client.publishEvents[T](name, event)
  }
}

case class EventTypesActions(client: Client) {
  import JacksonJsonMarshaller._
  def create(event: EventType) = {
    executeCall(client.createEventType(event))
  }
  def update(event: EventType)  = {
    executeCall(client.updateEventType(event.name, event))
  }
  def get(name: String)  = {
    executeCall(client.getEventType(name))
  }
  def getAll() = {
    executeCall(client.getEventTypes())
  }
  def delete(name: String) = {
    executeCall(client.deleteEventType(name))
  }

  private def executeCall[T](call: => Future[T]): T = {
    Await.result(call, 10.second)
  }
}

trait ModelFactory {
  val x = Random.alphanumeric
  def paritionKeyFields() = List("order_number")
  def schemaDefinition() = """{ "properties": { "order_number": { "type": "string" } } }"""
  def eventTypeSchema() = new EventTypeSchema(SchemaType.JSON, schemaDefinition)

  def createUniqueEventType(): EventType = {
    //Unique events mean unique name
    new EventType("test-client-integration-event-" + Random.nextInt() + "-" + Random.nextInt() + Random.nextInt(), //
      "laas-team", //
      EventTypeCategory.UNDEFINED, None, Nil, //
      Some(PartitionStrategy.RANDOM), Option(eventTypeSchema), //
      None, Option(paritionKeyFields), None)
  }
  def createEventMetadata(): EventMetadata = {
    val length = 5
    val eid = java.util.UUID.randomUUID.toString
    val occurredAt = Calendar.getInstance().toString()
    new EventMetadata(eid, None, occurredAt, None, Nil, None, None)
  }
}