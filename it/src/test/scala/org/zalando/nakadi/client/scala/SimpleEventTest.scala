package org.zalando.nakadi.client.scala

import org.scalatest.Matchers
import org.scalatest.WordSpec
import org.zalando.nakadi.client.scala.model.Cursor
import org.zalando.nakadi.client.scala.model.JacksonJsonMarshaller
import org.zalando.nakadi.client.scala.model.PartitionStrategyType
import org.zalando.nakadi.client.scala.test.factory.EventIntegrationHelper
import org.zalando.nakadi.client.scala.test.factory.events.MySimpleEvent
import org.zalando.nakadi.client.scala.test.factory.events.SimpleEventListener
import org.zalando.nakadi.client.scala.model.PartitionStrategy
import org.scalatest.BeforeAndAfterAll
import org.zalando.nakadi.client.scala.model.EventValidationStrategy

class SimpleEventTest extends WordSpec with Matchers with BeforeAndAfterAll {

  import org.scalatest.Matchers._
  import ClientFactory._
  import JacksonJsonMarshaller._
  import MySimpleEvent._

  val client = ClientFactory.getScalaClient()
  val nrOfEvents = 45
  val listener = new SimpleEventListener()

  override def afterAll {
    client.stop()
  }

  "404 should be handled graciously, by retuning None" in {
    val eventGenerator = new DefaultMySimpleEventGenerator() {
      def eventTypeId = s"SimpleEventIntegrationTest-Handle-404-Graciously"
    }
    val it = new EventIntegrationHelper(eventGenerator, client)
    it.createEventType() shouldBe true

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
    it.createEventType() shouldBe true
    val events = it.publishEvents(nrOfEvents)
    client.subscribe(eventGenerator.eventTypeName, StreamParameters(cursor = cursor), listener)
    val receivedEvents = listener.waitToReceive(nrOfEvents)
    receivedEvents.size shouldBe events.size
    receivedEvents shouldBe events
  }

  "Validate created EventType" in {
    val eventGenerator = new DefaultMySimpleEventGenerator() {
      def eventTypeId = s"SimpleEventIntegrationTest-Validate-Created-EventType"
    }
    val it = new EventIntegrationHelper(eventGenerator, client)

    it.createEventType() shouldBe true

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

  "Validate nr of partitions after Creation of EventType" in {
    val eventGenerator = new DefaultMySimpleEventGenerator() {
      def eventTypeId = s"SimpleEventIntegrationTest-Validate-nr-of-partitions"
    }
    val it = new EventIntegrationHelper(eventGenerator, client)
    it.createEventType() shouldBe true
    it.getNumberOfPartitions() shouldBe 1
  }

  "Receive partition-strategies successfully" ignore {
    val eventGenerator = new DefaultMySimpleEventGenerator() {
      def eventTypeId = s"SimpleEventIntegrationTest-Receive-partition-strategies-successfully"
    }
    val it = new EventIntegrationHelper(eventGenerator, client)
    val result = it.getPartitionStrategies()
    result.size shouldBe 3 //NOT IMPLEMENTED
    result should contain (PartitionStrategy.HASH)
    result should contain (PartitionStrategy.RANDOM)
    result should contain (PartitionStrategy.USER_DEFINED)
  }
  "Receive validation-strategies successfully" ignore {
    val eventGenerator = new DefaultMySimpleEventGenerator() {
      def eventTypeId = s"SimpleEventIntegrationTest-Receive-validation-strategies-successfully"
    }
    val it = new EventIntegrationHelper(eventGenerator, client)
    val result = it.getValidationStrategies()
    result.size shouldBe 0 //NOT IMPLEMENTED
  }

  "Receive enrichment-strategies successfully" ignore {
    val eventGenerator = new DefaultMySimpleEventGenerator() {
      def eventTypeId = s"SimpleEventIntegrationTest-Receive-enrichment-strategies-successfully"
    }
    val it = new EventIntegrationHelper(eventGenerator, client)
    val result = it.getValidationStrategies()
    result.size shouldBe 1

    result should contain(EventValidationStrategy.SCHEMA_VALIDATION)

  }

}

