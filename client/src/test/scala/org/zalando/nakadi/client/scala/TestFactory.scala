package org.zalando.nakadi.client.scala

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.Random
import org.joda.time.DateTime
import org.zalando.nakadi.client.model.Event
import org.zalando.nakadi.client.model.EventMetadata
import org.zalando.nakadi.client.model.EventMetadata
import org.zalando.nakadi.client.model.EventType
import org.zalando.nakadi.client.model.EventTypeCategory
import org.zalando.nakadi.client.model.EventTypeSchema
import org.zalando.nakadi.client.model.JacksonJsonMarshaller
import org.zalando.nakadi.client.model.PartitionStrategy
import org.zalando.nakadi.client.model.SchemaType
import org.zalando.nakadi.client.model.JacksonJsonMarshaller
import org.zalando.nakadi.client.Serializer
import org.zalando.nakadi.client.model.JacksonJsonMarshaller
import org.zalando.nakadi.client.Deserializer

trait ClientFactory {
  val host = "nakadi-sandbox.aruha-test.zalan.do"
  val OAuth2Token = () => ""
  val port = 443
  val connection = Connection.newConnection(host, port, OAuth2Token, true, false)
  val client = new ClientImpl(connection, "UTF-8")
  

}

case class EventActions(client: Client)   {
import JacksonJsonMarshaller._
  def create[T](name: String, event: Seq[T])(implicit ser: Serializer[Seq[T]]) = {
    client.publishEvents[T](name, event)
  }
}

case class EventTypesActions(client: Client)  {
import JacksonJsonMarshaller._
  def create(event: EventType)(implicit ser: Serializer[EventType]) = {
    executeCall(client.createEventType(event))
  }
  def update(event: EventType)(implicit ser: Serializer[EventType]) = {
    executeCall(client.updateEventType(event.name, event))
  }
  def get(name: String)(implicit ser: Deserializer[Option[EventType]]) = {
    executeCall(client.getEventType(name))
  }
  def getAll()(implicit ser: Deserializer[Option[Seq[EventType]]]) = {
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
    val occurredAt = new DateTime().toString()
    new EventMetadata(eid, None, occurredAt, None, Nil, None, None)
  }
}