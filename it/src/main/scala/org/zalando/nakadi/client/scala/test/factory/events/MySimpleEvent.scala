package org.zalando.nakadi.client.scala.test.factory.events

import org.zalando.nakadi.client.scala.model._
import com.fasterxml.jackson.core.`type`.TypeReference
import org.zalando.nakadi.client.scala.model.EventStreamBatch
import org.zalando.nakadi.client.scala.test.factory.EventGenerator
import scala.util.Random
import org.zalando.nakadi.client.scala.Listener
import org.zalando.nakadi.client.scala.ClientError
import org.slf4j.LoggerFactory
import org.slf4j.Logger

object MySimpleEvent {

  trait DefaultMySimpleEventGenerator extends EventGenerator {

    def newId: String = Random.alphanumeric.take(12).mkString

    def newEvent(): Event = new MySimpleEvent(newId)

    lazy val eventTypeName: String = eventTypeId + newId

    def schemaDefinition: String = """{ "properties": { "order_number": { "type": "string" } } }"""
  }

  implicit def myEventExampleTR: TypeReference[EventStreamBatch[MySimpleEvent]] = new TypeReference[EventStreamBatch[MySimpleEvent]] {}
}

case class MySimpleEvent(orderNumber: String) extends Event

class SimpleEventListener extends Listener[MySimpleEvent] {
  private val log = LoggerFactory.getLogger(this.getClass)
  var receivedEvents = List[MySimpleEvent]()
  def id = "SimpleEventListener"
  def onError(sourceUrl: String, error: Option[ClientError]): Unit = {
    log.error(s"Error $sourceUrl $error")
  }

  def onReceive(sourceUrl: String, cursor: Cursor, events: Seq[MySimpleEvent]): Unit = {
    receivedEvents = receivedEvents ++ events
    log.info(s"Received ${events.size}")
    log.info(s"Total ${receivedEvents.size}")

  }
  def onSubscribed(endpoint: String, cursor: Option[Cursor]): Unit = {
    log.info(s"Endpoint $endpoint - cursor $cursor")

  }

  def waitToReceive(nrOfEvents: Int): Seq[MySimpleEvent] = {
    while (nrOfEvents > receivedEvents.size) {
      Thread.sleep(2000) // Wait 2 seconds
    }
    log.info("############")
    log.info(s"Waited to receive $nrOfEvents events, actual size =${receivedEvents.size}")
    log.info("############")
    receivedEvents
  }
}

 