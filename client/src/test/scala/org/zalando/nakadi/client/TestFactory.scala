package org.zalando.nakadi.client

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.Random
import org.joda.time.format.ISODateTimeFormat
import org.zalando.nakadi.client.model.Event
import org.zalando.nakadi.client.model.EventMetadata
import org.zalando.nakadi.client.model.EventType
import org.zalando.nakadi.client.model.EventTypeCategory
import org.zalando.nakadi.client.model.EventTypeSchema
import org.zalando.nakadi.client.model.PartitionResolutionStrategy
import org.zalando.nakadi.client.model.SchemaType
//import org.zalando.nakadi.client.model.SprayJsonMarshaller
import org.joda.time.DateTime
import org.zalando.nakadi.client.model.EventMetadata
import org.zalando.nakadi.client.model.JacksonJsonMarshaller

trait ClientFactory {
  val host = ""
  val OAuth2Token = () => ""
  val port = 443
  val client = new ClientImpl(Connection.newConnection(host, port, OAuth2Token, true, false), "UTF-8")

  
}

case class EventTypesActions(client: Client) extends JacksonJsonMarshaller{

  def create(event: EventType)(implicit ser: Serializer[EventType]) = {
    executeCall(client.newEventType(event))
  }
  def update(event: EventType)(implicit ser: Serializer[EventType]) = {
    executeCall(client.updateEventType(event.name, event))
  }
  def get(name: String)(implicit ser: Deserializer[EventType]) = {
    executeCall(client.eventType(name))
  }
  def getAll()(implicit ser: Deserializer[Seq[EventType]]) = {
    executeCall(client.eventTypes())
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
  def partitionStrategy() = new PartitionResolutionStrategy("hash", None)
  def paritionKeyFields() = List("order_number")
  def schemaDefinition() = """{ "properties": { "order_number": { "type": "string" } } }"""
  def eventTypeSchema() = new EventTypeSchema(SchemaType.JSON, schemaDefinition)
  
  def createUniqueEventType(): EventType = {
    //Unique events mean unique name
    new EventType("test-client-integration-event-" + Random.nextInt() + "-" + Random.nextInt() + Random.nextInt(), //
      "laas-team", //
      EventTypeCategory.BUSINESS, None, None, //
      partitionStrategy, Option(eventTypeSchema), //
     None,  Option(paritionKeyFields), None)
  }
  def createEventMetadata(): EventMetadata = {
    val length=5
    val eid= java.util.UUID.randomUUID.toString
    val occurredAt = new DateTime().toString()
    new EventMetadata(eid,None,occurredAt,None,Nil,None,None)
  }
}