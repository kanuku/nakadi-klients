package org.zalando.nakadi.client.scala.test.factory

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import org.zalando.nakadi.client.scala.model._
import org.zalando.nakadi.client.scala.model._
import org.zalando.nakadi.client.scala.Client

case class ClientActions(client: Client) {
  def publish[T <: Event](name: String, event: Seq[T]) = {
    executeCall(client.publishEvents[T](name, event))
  }

  import JacksonJsonMarshaller._
  def createEventType(event: EventType) = {
    executeCall(client.createEventType(event))
  }
  def updateEventType(event: EventType) = {
    executeCall(client.updateEventType(event.name, event))
  }
  def getEventType(name: String) = {
    executeCall(client.getEventType(name))
  }
  def getEventTypes() = {
    executeCall(client.getEventTypes())
  }
  def deleteEventType(name: String) = {
    executeCall(client.deleteEventType(name))
  }

  private def executeCall[T](call: => Future[T]): T = {
    Await.result(call, 10.second)
  }

}