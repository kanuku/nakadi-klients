package org.zalando.nakadi.client

import org.scalatest.{ Matchers, WordSpec }
import org.zalando.nakadi.client.model._

class EventTypeTest extends WordSpec with Matchers with JacksonJsonMarshaller with ModelFactory with ClientFactory {

  val events = new EventTypesActions(client)
  "POST/PUT/GET/DELETE single EventType " in {

    //Create event 
    val eventType = createUniqueEventType()
    val creationResult = events.create(eventType)
    creationResult.isDefined shouldBe false

    //Check the created EventType
    checkEventTypeExists(eventType)

    //TODO: Enable this when PUT is supported.
    //    Update the event
    //        val updatedEvent = eventType.copy(owningApplication = "laas-team-2")
    //        events.update(updatedEvent)

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
    //TODO: Enable when Nakadi has no erranous eventType
    //    val Right(Some(allEvents)) = events.getAll()
    //    allEvents should contain(eventType1)
    //    allEvents should contain(eventType2)

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

  //TODO: Enable when implemented
  "UpdateEventTypes" in {
    //Create 2 EventTypes
    val eventType = createUniqueEventType()

    events.create(eventType)
    checkEventTypeExists(eventType)

    //Update the event
    val updatedEvent = eventType.copy(owningApplication = "laas-team-2")
    events.update(updatedEvent)

    //Check the EventType has bee updated
    //    checkEventTypeExists(updatedEvent)
    //    checkEventTypeDoesNotExist(eventType)

  }

  def checkEventTypeDoesNotExist(eventType: EventType) = {
    val requestedEvent = events.get(eventType.name)
    println(requestedEvent)
    requestedEvent.isRight shouldBe true
    val Right(result) = requestedEvent
    result shouldBe None
  }

  def checkEventTypeExists(eventType: EventType) = {
    val Right(Some(createdEvent)) = events.get(eventType.name)
    createdEvent shouldBe eventType
  }

}

