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

class EventIntegrationTest(generator: EventGenerator, client: Client) extends WordSpec with Matchers {

  val actions = ClientActions(client)
  //fixed Variables that should not be generated twice
  val eventType = generator.eventType

  def createEventType(): EventType = {
    failIfClientError(actions.createEventType(eventType))
    eventType
  }

  def deleteEventType() = failIfClientError(actions.deleteEventType(eventType.name))

  def publishEvents(nrOfEvents: Int): Seq[Event] = {
    val events = for {
      a <- 0 to nrOfEvents
    } yield generator.newEvent()
    failIfClientError(actions.publish(eventType.name, events))
    events
  }

  //Private methods
  private def failIfClientError(in: Option[ClientError]) = in match {
    case Some(clientError) =>
      fail("Failed with clientError " + clientError)
    case _ =>
  }

}

 