package org.zalando.nakadi.client

import scala.concurrent.{ Await, Future }
import scala.concurrent.duration.DurationInt
import scala.util.Random

import org.scalatest.{ Matchers, WordSpec }
import org.zalando.nakadi.client.model.{ EventType, EventTypeCategory, EventTypeSchema, PartitionResolutionStrategy, SchemaType, SprayJsonMarshaller }

class EventTypeTest extends WordSpec with Matchers with SprayJsonMarshaller {

  //Client configuration
  val host = ""
  val OAuth2Token = () => ""
  val port = 443
  val client = new ClientImpl(Connection.newConnection(host, port, OAuth2Token, true, false), "UTF-8")
  val events = new EventTypesActions(client)

  //EventType fields
  val partitionStrategy = new PartitionResolutionStrategy("hash", None)
  val paritionKeyFields = List("order_number")
  val schemaDefinition = """{ "properties": { "order_number": { "type": "string" } } }"""
  val eventTypeSchema = new EventTypeSchema(SchemaType.JSON, schemaDefinition)

  "POST/PUT/GET/DELETE single EventType " in {

    //Create event
    val eventType = createUniqueEventType()
    val creationResult = events.create(eventType)
    creationResult.isDefined shouldBe false

    //Check the created EventType
    checkEventTypeExists(eventType)

    
    //TODO: Enable this when PUT is supported.
    //Update the event
//    val updatedEvent = eventType.copy(owningApplication = "laas-team-2")
//    events.update(updatedEvent)

    //Check the EventType has bee updated
//    checkEventTypeExists(updatedEvent)
//    checkEventTypeDoesNotExist(eventType)

    //Delete the created Event
    val deletedEvent = events.delete(eventType.name)
    deletedEvent.isEmpty shouldBe true

    //Is it really deleted?
    checkEventTypeDoesNotExist(eventType)
  }

  "POST/GET/DELETE multiple EventTypes " in {

    //Create 2 EventTypes
    val eventType1 = createUniqueEventType()
    val eventType2 = createUniqueEventType()

    events.create(eventType1)
    checkEventTypeExists(eventType1)

    events.create(eventType2)
    checkEventTypeExists(eventType2)

    //Get all EventTypes again
    val Right(Some(allEvents)) = events.getAll()
    allEvents should contain(eventType1)
    allEvents should contain(eventType2)

    //Delete the 2 EventTypes
    events.delete(eventType1.name)
    events.delete(eventType2.name)

    //Check if the're really deleted
    checkEventTypeDoesNotExist(eventType1)
    checkEventTypeDoesNotExist(eventType2)

    //Get all should not contain the deleted events
    val Right(Some(updatedEvents)) = events.getAll()

    updatedEvents shouldNot contain(eventType1)
    updatedEvents shouldNot contain(eventType2)

  }

  def checkEventTypeDoesNotExist(eventType: EventType) = {
    val requestedEvent = events.get(eventType.name)
    requestedEvent.isLeft shouldBe true
    val Left(result) = requestedEvent
    result.status shouldBe Some(404)
  }

  def checkEventTypeExists(eventType: EventType) = {
    val Right(Some(createdEvent)) = events.get(eventType.name)
    createdEvent shouldBe eventType
  }

  private def createUniqueEventType(): EventType = {
    new EventType("test-client-integration-event-" + Random.nextInt() + "-" + Random.nextInt() + Random.nextInt(), //
      "laas-team", //
      EventTypeCategory.BUSINESS, None, None, //
      partitionStrategy, Option(eventTypeSchema), //
      Option(paritionKeyFields), None, None)
  }

}

class EventTypesActions(client: Client) extends SprayJsonMarshaller {

  def create(event: EventType) = {
    executeCall(client.newEventType(event))
  }
  def update(event: EventType) = {
    executeCall(client.updateEventType(event.name, event))
  }
  def get(name: String) = {
    executeCall(client.eventType(name))
  }
  def getAll() = {
    executeCall(client.eventTypes())
  }
  def delete(name: String) = {
    executeCall(client.deleteEventType(name))
  }

  private def executeCall[T](call: => Future[T]): T = {
    Await.result(call, 10.second)
  }
}