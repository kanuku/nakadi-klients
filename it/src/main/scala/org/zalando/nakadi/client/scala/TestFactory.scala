package org.zalando.nakadi.client.scala

import java.util.Calendar

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.Random

import org.zalando.nakadi.client.scala.model._
import com.fasterxml.jackson.core.`type`.TypeReference
import org.zalando.nakadi.client.scala.model._



sealed trait TestUtil {
  
    def executeCall[T](call: => Future[T]): T = {
    Await.result(call, 10.second)
  }
}

case class EventActions(client: Client)extends TestUtil {
  def publish[T <: Event](name: String, event: Seq[T]) = {
    executeCall(client.publishEvents[T](name, event))
  }
}

case class EventTypesActions(client: Client)extends TestUtil {
  import JacksonJsonMarshaller._
  def create(event: EventType) = {
    executeCall(client.createEventType(event))
  }
  def update(event: EventType) = {
    executeCall(client.updateEventType(event.name, event))
  }
  def get(name: String) = {
    executeCall(client.getEventType(name))
  }
  def getAll() = {
    executeCall(client.getEventTypes())
  }
  def delete(name: String) = {
    executeCall(client.deleteEventType(name))
  }


}
 

trait ModelFactory {
  val x = Random.alphanumeric
  case class MyEventExample(orderNumber: String) extends Event
  implicit def myEventExampleTR: TypeReference[EventStreamBatch[MyEventExample]] = new TypeReference[EventStreamBatch[MyEventExample]] {}
  //EventType
  def paritionKeyFields() = List("order_number")
  def eventTypeSchema() = new EventTypeSchema(SchemaType.JSON, schemaDefinition)
  def schemaDefinition() = """{ "properties": { "order_number": { "type": "string" } } }"""

  def createEventType(name: String,
                      owningApplication: String = "nakadi-klients"): EventType = {
    new EventType(name, //
      owningApplication, //
      EventTypeCategory.UNDEFINED, Nil, Nil, //
      Some(PartitionStrategy.RANDOM), eventTypeSchema, //
      Nil, paritionKeyFields, None)
  }
  def createEventMetadata(): EventMetadata = {
    val length = 5
    val eid = java.util.UUID.randomUUID.toString
    val occurredAt = Calendar.getInstance().toString()
    new EventMetadata(eid, None, occurredAt, None, Nil, None, None)
  }
}