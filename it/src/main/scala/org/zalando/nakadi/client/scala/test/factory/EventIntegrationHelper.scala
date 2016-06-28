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
  val eventType = generator.eventType

  def createEventType(): Boolean = {
    wasItsuccessfull(executeCall(client.createEventType(eventType)), "createEventType")
  }

  def getEventType(eventTypeName: String = eventType.name): Option[EventType] = executeCall(client.getEventType(eventTypeName)) match {
    case Left(clientError) =>
      wasItsuccessfull(Option(clientError), "getEventType")
      None
    case Right(result) => result
  }

  def deleteEventType() = wasItsuccessfull(executeCall(client.deleteEventType(eventType.name)), "deleteEventType")

  def publishEvents(nrOfEvents: Int): Seq[Event] = {
    val events = for {
      a <- 1 to nrOfEvents
    } yield generator.newEvent()
    wasItsuccessfull(executeCall(client.publishEvents(eventType.name, events)), "publishEvents")
    log.info(s"EVENTS published: $events")
    events
  }

  def updateEventType(eType: EventType): Boolean = {
    wasItsuccessfull(executeCall(client.updateEventType(eventType.name, eType)), "updateEventType")
  }

  def eventTypeExist(eventTypeName: String = eventType.name): Boolean = {
    getEventType(eventTypeName) match {
      case None    => false
      case Some(_) => true
    }
  }

  def getNumberOfPartitions(): Int = extractFromRightOptional(() => client.getPartitions(eventType.name)).size

  private def extractFromRightOptional[T](function: () => Future[Either[ClientError, Option[T]]]) = {
    val received = executeCall(function())
    assert(received.isRight == true, s"ClientErrror ${received}")
    val Right(opt) = received
    assert(opt.isDefined == true, "because it expected Some, but received None")
    val Some(eventTypes) = opt
    eventTypes
  }


  def getEnrichmentStrategies(): Seq[EventEnrichmentStrategy.Value] = extractFromRightOptional(() => client.getEnrichmentStrategies())
  def getPartitionStrategies(): Seq[PartitionStrategy.Value] = extractFromRightOptional(() => client.getPartitioningStrategies())

  //Private methods
  private def wasItsuccessfull(in: Option[ClientError], msg: String): Boolean = in match {
    case Some(clientError) =>
      log.error(s"${generator.eventTypeId} - ${msg} => ClientError: $clientError")
      false
    case None =>
      true
  }

  private def executeCall[T](call: => Future[T]): T = {
    Await.result(call, 10.second)
  }

}

 