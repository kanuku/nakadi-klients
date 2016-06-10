package org.zalando.nakadi.client.scala.test.factory

import scala.concurrent.duration.DurationInt

import org.scalatest.Matchers
import org.scalatest.WordSpec
import org.zalando.nakadi.client.scala.Client
import org.zalando.nakadi.client.scala.Client
import org.zalando.nakadi.client.scala.ClientError
import org.zalando.nakadi.client.scala.model._
import org.zalando.nakadi.client.scala.model._
import org.zalando.nakadi.client.scala.model._
import org.zalando.nakadi.client.scala.model.Event
import org.slf4j.LoggerFactory

class EventIntegrationHelper(generator: EventGenerator, client: Client) extends WordSpec with Matchers {
  private val log = LoggerFactory.getLogger(this.getClass)
  val actions = ClientActions(client)
  val eventType = generator.eventType

  def createEventType(): EventType = {
    failIfClientError(actions.createEventType(eventType))
    eventType
  }

  def deleteEventType() = failIfClientError(actions.deleteEventType(eventType.name))
  
  

  def publishEvents(nrOfEvents: Int): Seq[Event] = {
    val events = for {
      a <- 1 to nrOfEvents
    } yield generator.newEvent()
    failIfClientError(actions.publish(eventType.name, events))
    log.info(s"EVENTS published: $events")
    events
  }

  //Private methods
  private def failIfClientError(in: Option[ClientError]) = in match {
    case Some(clientError) =>
      fail("Failed with clientError " + clientError)
    case _ =>
  }

}

 