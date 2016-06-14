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

  def createEventType(): Boolean = {
    wasItsuccessfull(executeCall(client.createEventType(eventType)))
  }

  def getEventType(eventTypeName: String = eventType.name): Option[EventType] = executeCall(client.getEventType(eventTypeName)) match {
    case Left(clientError) =>
      wasItsuccessfull(Option(clientError))
      None
    case Right(result) => result
  }

  def deleteEventType() = wasItsuccessfull(actions.deleteEventType(eventType.name))

  def publishEvents(nrOfEvents: Int): Seq[Event] = {
    val events = for {
      a <- 1 to nrOfEvents
    } yield generator.newEvent()
    wasItsuccessfull(actions.publish(eventType.name, events))
    log.info(s"EVENTS published: $events")
    events
  }

  def updateEventType(eType: EventType): Boolean = {
    wasItsuccessfull(executeCall(client.updateEventType(eventType.name, eType)))
  }

  def eventTypeExist(eventTypeName: String = eventType.name): Boolean = {
    getEventType(eventTypeName) match {
      case None    => false
      case Some(_) => true
    }
  }

  //Private methods
  private def wasItsuccessfull(in: Option[ClientError]): Boolean = in match {
    case Some(clientError) =>
      log.error("ClientError: {}", clientError)
      false
    case None =>
      true
  }

  private def executeCall[T](call: => Future[T]): T = {
    Await.result(call, 10.second)
  }

}

 