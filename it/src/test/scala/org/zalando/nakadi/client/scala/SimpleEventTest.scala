package org.zalando.nakadi.client.scala

import org.scalatest.BeforeAndAfterAll
import org.scalatest.Matchers
import org.scalatest.WordSpec
import org.zalando.nakadi.client.scala.model.Cursor
import org.zalando.nakadi.client.scala.model.EventEnrichmentStrategy
import org.zalando.nakadi.client.scala.model.PartitionStrategy
import org.zalando.nakadi.client.scala.model.EventTypeStatistics
import org.zalando.nakadi.client.scala.model.PartitionStrategyType
import org.zalando.nakadi.client.scala.model.ScalaJacksonJsonMarshaller
import org.zalando.nakadi.client.scala.test.factory.EventIntegrationHelper
import org.zalando.nakadi.client.scala.test.factory.events.MySimpleEvent
import org.zalando.nakadi.client.scala.test.factory.events.SimpleEventListener
import org.zalando.nakadi.client.utils.ClientBuilder
import org.scalatest.BeforeAndAfter
import org.slf4j.LoggerFactory

class SimpleEventTest extends WordSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll {
  import ClientFactory._
  import ScalaJacksonJsonMarshaller._
  import MySimpleEvent._

  private val client = ClientFactory.buildScalaClient()
  private var itEvent:EventIntegrationHelper = null
  
  val nrOfEvents = 45

  after{
    
    itEvent.deleteEventType()
  }
  override def afterAll {
        client.stop()
  }

  "404_should be handled graciously" in {
    val eventGenerator = new DefaultMySimpleEventGenerator() {
      def eventTypeId = s"SimpleEventIntegrationTest-Handle-404-Graciously"
    }
    itEvent = new EventIntegrationHelper(eventGenerator, client)
    itEvent.createEventType() shouldBe true

    itEvent.getEventType("non-existing-event-type-name") match {
      case Some(_) => fail("Should not fail, because eventType was not created yet!!")
      case None    =>
    }
  }

  "Validate Published nr of SimpleEvents" in {
    val eventGenerator = new DefaultMySimpleEventGenerator() {
      def eventTypeId = s"SimpleEventIntegrationTest-Validate-Published-Events-$nrOfEvents"
    }
    itEvent = new EventIntegrationHelper(eventGenerator, client)
    val cursor = Some(Cursor("0", "BEGIN"))
    val listener = new SimpleEventListener()

    itEvent.createEventType() shouldBe true
    Thread.sleep(2000)
    val events = itEvent.publishEvents(nrOfEvents)
    client.subscribe(eventGenerator.eventTypeName, StreamParameters(cursor = cursor), listener)
    val receivedEvents = listener.waitToReceive(nrOfEvents)
    receivedEvents.size shouldBe events.size
    receivedEvents shouldBe events
  }

  "Multiple events listeners must work in parallel" in {
    val eventGenerator = new DefaultMySimpleEventGenerator() {
      def eventTypeId = s"SimpleEventIntegrationTest-Multiple-listeners-in-parallel-$nrOfEvents"
    }
    itEvent = new EventIntegrationHelper(eventGenerator, client)
    val cursor = Some(Cursor("0", "BEGIN"))
    itEvent.createEventType() shouldBe true
    val events = itEvent.publishEvents(nrOfEvents)
    val listener = new SimpleEventListener()
    val listener2 = new SimpleEventListener()
    val listener3 = new SimpleEventListener()

    client.subscribe(eventGenerator.eventTypeName, StreamParameters(cursor = cursor), listener)
    client.subscribe(eventGenerator.eventTypeName, StreamParameters(cursor = cursor), listener2)

    val receivedEvents = listener.waitToReceive(nrOfEvents)
    receivedEvents.size shouldBe events.size
    receivedEvents shouldBe events

    val receivedEvents2 = listener2.waitToReceive(nrOfEvents)
    receivedEvents2.size shouldBe events.size
    receivedEvents2 shouldBe events

    client.subscribe(eventGenerator.eventTypeName, StreamParameters(cursor = cursor), listener)
    val receivedEvents3 = listener.waitToReceive(nrOfEvents * 2)
    receivedEvents3.size shouldBe (nrOfEvents * 2)

  }

  "An unsubscribed listener should not receive any events" in {
    val eventGenerator = new DefaultMySimpleEventGenerator() {
      def eventTypeId = s"SimpleEventIntegrationTest-Multiple-listeners-in-parallel-$nrOfEvents"
    }
    itEvent = new EventIntegrationHelper(eventGenerator, client)
    val cursor = Some(Cursor("0", "BEGIN"))
    itEvent.createEventType() shouldBe true
    val listener = new SimpleEventListener()
    client.subscribe(eventGenerator.eventTypeName, StreamParameters(cursor = cursor), listener)
    client.unsubscribe(eventGenerator.eventTypeName, Option("0"), listener)
    itEvent.publishEvents(nrOfEvents)
    Thread.sleep(5000)
    listener.receivedEvents.size shouldBe 0
  }
  "Validate created EventType" in {
    val eventGenerator = new DefaultMySimpleEventGenerator() {
      def eventTypeId = s"SimpleEventIntegrationTest-Validate-Created-EventType"
    }
    itEvent = new EventIntegrationHelper(eventGenerator, client)

    itEvent.createEventType() shouldBe true

    val optionalOfCreatedEventType = itEvent.getEventType()

    optionalOfCreatedEventType.isDefined shouldBe true

    val Some(eventType) = optionalOfCreatedEventType
    eventType.category shouldBe itEvent.eventType.category
    eventType.dataKeyFields shouldBe null
    eventType.name shouldBe itEvent.eventType.name
    eventType.owningApplication shouldBe itEvent.eventType.owningApplication
    eventType.partitionStrategy shouldBe itEvent.eventType.partitionStrategy
    eventType.schema shouldBe itEvent.eventType.schema
    eventType.statistics shouldBe itEvent.eventType.statistics
    eventType.enrichmentStrategies shouldBe itEvent.eventType.enrichmentStrategies
    eventType.partitionKeyFields shouldBe itEvent.eventType.partitionKeyFields
  }
  "Validate created EventType wiht Statistics" in {
      val eventGenerator = new DefaultMySimpleEventGenerator() {
          def eventTypeId = s"SimpleEventIntegrationTest-Validate-Created-EventType"
          override def statistics: Option[EventTypeStatistics] = Option(EventTypeStatistics(2400, 20240, 4, 4))
      }
      itEvent = new EventIntegrationHelper(eventGenerator, client)
      
      itEvent.createEventType() shouldBe true
      
      Thread.sleep(5000)
      
      val optionalOfCreatedEventType = itEvent.getEventType()
      
      optionalOfCreatedEventType.isDefined shouldBe true
      
      val Some(eventType) = optionalOfCreatedEventType
      eventType.category shouldBe itEvent.eventType.category
      eventType.dataKeyFields shouldBe null
      eventType.name shouldBe itEvent.eventType.name
      eventType.owningApplication shouldBe itEvent.eventType.owningApplication
      eventType.partitionStrategy shouldBe itEvent.eventType.partitionStrategy
      eventType.schema shouldBe itEvent.eventType.schema
      eventType.statistics shouldBe itEvent.eventType.statistics
      eventType.enrichmentStrategies shouldBe itEvent.eventType.enrichmentStrategies
      eventType.partitionKeyFields shouldBe itEvent.eventType.partitionKeyFields
      eventType.statistics should not be null 
      eventType.statistics.get.messageSize shouldBe itEvent.eventType.statistics.get.messageSize
      eventType.statistics.get.messagesPerMinute shouldBe itEvent.eventType.statistics.get.messagesPerMinute
      eventType.statistics.get.readParallelism shouldBe itEvent.eventType.statistics.get.readParallelism
      eventType.statistics.get.writeParallelism shouldBe itEvent.eventType.statistics.get.writeParallelism
      
  }

  "Validate nr of partitions after Creation of EventType" in {
    val eventGenerator = new DefaultMySimpleEventGenerator() {
      def eventTypeId = s"SimpleEventIntegrationTest-Validate-nr-of-partitions"
    }
    itEvent = new EventIntegrationHelper(eventGenerator, client)
    itEvent.createEventType() shouldBe true
    itEvent.getNumberOfPartitions() shouldBe 1
  }

  "Receive partition-strategies successfully" ignore {
    val eventGenerator = new DefaultMySimpleEventGenerator() {
      def eventTypeId = s"SimpleEventIntegrationTest-Receive-partition-strategies-successfully"
    }
    itEvent = new EventIntegrationHelper(eventGenerator, client)
    val result = itEvent.getPartitionStrategies()
    result.size shouldBe 3 //NOT IMPLEMENTED
    result should contain(PartitionStrategy.HASH)
    result should contain(PartitionStrategy.RANDOM)
    result should contain(PartitionStrategy.USER_DEFINED)
  }

  "Receive enrichment-strategies successfully" ignore {
    val eventGenerator = new DefaultMySimpleEventGenerator() {
      def eventTypeId = s"SimpleEventIntegrationTest-Receive-enrichment-strategies-successfully"
    }
    itEvent = new EventIntegrationHelper(eventGenerator, client)
    val result = itEvent.getEnrichmentStrategies()
    result.size shouldBe 1
    result should contain(EventEnrichmentStrategy.METADATA)
  }
  
}

