package org.zalando.nakadi.client.scala

import org.scalatest.Matchers
import org.scalatest.WordSpec
import org.zalando.nakadi.client.scala.model.Cursor
import org.zalando.nakadi.client.scala.model.JacksonJsonMarshaller
import org.zalando.nakadi.client.scala.test.factory.EventIntegrationHelper
import org.zalando.nakadi.client.scala.test.factory.events.MySimpleEvent
import org.zalando.nakadi.client.scala.test.factory.events.SimpleEventListener

class SimpleEventTest extends WordSpec with Matchers {

  import org.scalatest.Matchers._

  import ClientFactory._
  import JacksonJsonMarshaller._
  import MySimpleEvent._

  val client = ClientFactory.getScalaClient()
  val nrOfEvents = 45
  val listener = new SimpleEventListener()

  "404 should be handled graciously, by retuning None" in {
    val it = new EventIntegrationHelper(null, client)
    it.getEventType("non-existing-event-type-name") match {
      case Some(_) => fail("Should not fail, because eventType was not created yet!!")
      case None    =>
    }

  }

  "Validate Published nr of SimpleEvents" in {

    val eventGenerator = new DefaultMySimpleEventGenerator() {
      def eventTypeId = s"SimpleEventIntegrationTest-Validate-Published-Events-$nrOfEvents"
    }
    val it = new EventIntegrationHelper(eventGenerator, client)
    val cursor = Some(Cursor("0", "BEGIN"))

    it.createEventType()
    val events = it.publishEvents(nrOfEvents)

    client.subscribe(eventGenerator.eventTypeName, StreamParameters(cursor = cursor), listener)
    val receivedEvents = listener.waitToReceive(nrOfEvents)

    receivedEvents.size shouldBe events.size
    receivedEvents shouldBe events

  }
  "Validate created EventType" in {
    val eventGenerator = new DefaultMySimpleEventGenerator() {
      def eventTypeId = s"SimpleEventIntegrationTest-Validate-Created-Events-$nrOfEvents"
    }
    val it = new EventIntegrationHelper(eventGenerator, client)
    it.createEventType()
    val optionalOfCreatedEventType = it.getEventType()
    optionalOfCreatedEventType.isDefined shouldBe true
    val Some(eventType) = optionalOfCreatedEventType
    eventType.category shouldBe it.eventType.category
    eventType.dataKeyFields shouldBe null
    eventType.name shouldBe it.eventType.name
    eventType.owningApplication shouldBe it.eventType.owningApplication
    eventType.partitionStrategy shouldBe it.eventType.partitionStrategy
    eventType.schema shouldBe it.eventType.schema
    eventType.statistics shouldBe it.eventType.statistics
    eventType.validationStrategies shouldBe null
    eventType.enrichmentStrategies shouldBe it.eventType.enrichmentStrategies
    eventType.partitionKeyFields shouldBe it.eventType.partitionKeyFields

  }

}

