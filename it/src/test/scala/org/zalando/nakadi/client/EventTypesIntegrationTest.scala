package org.zalando.nakadi.client

import org.scalatest.{ Matchers, WordSpec }
import org.zalando.nakadi.client.scala.model._
import com.fasterxml.jackson.core.`type`.TypeReference
import org.zalando.nakadi.client.scala.ClientFactory
import org.zalando.nakadi.client.scala.EventTypesActions
import org.zalando.nakadi.client.scala.EventActions
import org.zalando.nakadi.client.scala.ModelFactory

class EventTypeTest extends WordSpec with Matchers with ModelFactory{
  import ClientFactory._
import JacksonJsonMarshaller._
  val eventAction = new EventActions(client)
  val eventTypeAction = new EventTypesActions(client)
  "POST/PUT/GET/DELETE single EventType " in {
    val eventTypeName="test-client-integration-event-"
    //Create event 
    val eventType = createEventType(eventTypeName)
    val creationResult = eventTypeAction.create(eventType)
    creationResult.isDefined shouldBe false

    //Check the created EventType
    checkEventTypeExists(eventType)

    case class MyEventExample(orderNumber: String)extends Event
    implicit def problemTR: TypeReference[MyEventExample] = new TypeReference[MyEventExample] {}
    val events = for {
      a <- 0 to 12
    } yield MyEventExample("order-"+a)
//    eventAction.create("test-client-integration-event-1936085527-148383828851369665",  List(MyEventExample("test-1")))
    eventAction.publish(eventTypeName, events)

    //TODO: Enable this when PUT is supported.
    //    Update the event
    //        val updatedEvent = eventType.copy(owningApplication = "laas-team-2")
    //        events.update(updatedEvent)

    //Check the EventType has bee updated
    //    checkEventTypeExists(updatedEvent)
    //    checkEventTypeDoesNotExist(eventType)

    //Delete the created Event
    val deletedEvent = eventTypeAction.delete(eventType.name)
    deletedEvent.isEmpty shouldBe true

    //Is it really deleted?
    checkEventTypeDoesNotExist(eventType)
  }

  "POST/GET/DELETE multiple EventTypes " in {
val eventTypeName1="test-client-integration-event-1936085527-1"
val eventTypeName2="test-client-integration-event-1936085527-2"
    //Create 2 EventTypes
    val eventType1 = createEventType(eventTypeName1)
    val eventType2 = createEventType(eventTypeName2)

    eventTypeAction.create(eventType1)
    checkEventTypeExists(eventType1)

    eventTypeAction.create(eventType2)
    checkEventTypeExists(eventType2)

    //Get all EventTypes again
    //TODO: Enable when Nakadi has no erranous eventType
    //    val Right(Some(allEvents)) = events.getAll()
    //    allEvents should contain(eventType1)
    //    allEvents should contain(eventType2)

    //Delete the 2 EventTypes
    eventTypeAction.delete(eventType1.name)
    eventTypeAction.delete(eventType2.name)

    //Check if the're really deleted
    checkEventTypeDoesNotExist(eventType1)
    checkEventTypeDoesNotExist(eventType2)

    //Get all should not contain the deleted events
    val Right(Some(updatedEvents)) = eventTypeAction.getAll()

    updatedEvents shouldNot contain(eventType1)
    updatedEvents shouldNot contain(eventType2)

  }

  //TODO: Enable when implemented
  "UpdateEventTypes" in {
    val eventTypeName1="test-client-integration-event-1936085527-3"
    //Create 2 EventTypes
    val eventType = createEventType(eventTypeName1)

    eventTypeAction.create(eventType)
    checkEventTypeExists(eventType)

    //Update the event
    val updatedEvent = eventType.copy(owningApplication = "laas-team-2")
    eventTypeAction.update(updatedEvent)

    //Check the EventType has bee updated
    //    checkEventTypeExists(updatedEvent)
    //    checkEventTypeDoesNotExist(eventType)

  }

  def checkEventTypeDoesNotExist(eventType: EventType) = {
    val requestedEvent = eventTypeAction.get(eventType.name)
    println(requestedEvent)
    requestedEvent.isRight shouldBe true
    val Right(result) = requestedEvent
    result shouldBe None
  }

  def checkEventTypeExists(eventType: EventType) = {
    val Right(Some(createdEvent)) = eventTypeAction.get(eventType.name)
    createdEvent shouldBe eventType
  }

}

