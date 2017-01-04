package org.zalando.nakadi.client.scala

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.util.Try

import org.slf4j.LoggerFactory
import org.zalando.nakadi.client.scala.model.EventType

object CleanupTestEvents extends App {
  private val log = LoggerFactory.getLogger(ClientFactory.getClass)

  deleteAllEventsCreatedByClientIntegrationTests()

  def deleteAllEventsCreatedByClientIntegrationTests() = {
    val eventNames = List("SimpleEvent", "ClientIntegrationTest", "PaymentCreatedDataChangeEventTest")
    val client = ClientFactory.buildScalaClient()
    val eventsOnNakadi = Await.result(client.getEventTypes(), 5.second)
    val filteredEvents = eventsOnNakadi match {
      case Right(Some(events)) if !events.isEmpty=>
        events.foreach(p=>println(">>"+p.owningApplication))
        val result = events.filter(p=> eventNames.exists(s =>p.name.startsWith(s)) && shouldBeRemoved(p.owningApplication) ||shouldBeRemoved(p.owningApplication) )
        Try(deleteEventTypesThatStartWith(result, client))
      case left =>
        log.info("Something went wrong: {}", left)
        Nil
    }
    client.stop()

  }
  
  def shouldBeRemoved(owningApplication:String)=(owningApplication!=null&&owningApplication.toLowerCase().contains("nakadi-klients"))

  private def deleteEventTypesThatStartWith(events: Seq[EventType], client: Client) = {
    log.info("######################### Start deleting {} events",events.size)
         events.map { event =>
          log.info("######################### Deleting event name: {} owningApplication: " + event.owningApplication, event.name)
          Await.result(client.deleteEventType(event.name), 5.second)
        }
    log.info("######################### Finished deleting events")
  }
}