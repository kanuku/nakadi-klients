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
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.concurrent.Future

class EventIntegrationHelper(generator: EventGenerator, client: Client) extends WordSpec with Matchers {
  private val log = LoggerFactory.getLogger(this.getClass)
  val actions = ClientActions(client)
  val eventType = generator.eventType

  def createEventType(): EventType = {
    failIfClientError(executeCall(client.createEventType(eventType)))
    eventType
  }

  def getEventType(eventTypeName: String = eventType.name): Option[EventType] = executeCall(client.getEventType(eventType.name)) match {
    case Left(clientError) =>
      failIfClientError(Option(clientError))
      None
    case Right(result) => result
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

  def executeCall[T](call: => Future[T]): T = {
    Await.result(call, 10.second)
  }

}

 