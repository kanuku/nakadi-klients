package org.zalando.nakadi.client.scala

import java.util.UUID
import java.util.concurrent.atomic.AtomicLong

import com.fasterxml.jackson.core.`type`.TypeReference
import org.slf4j.LoggerFactory
import org.zalando.nakadi.client.examples.scala.EventCreationExample.MeetingsEvent
import org.zalando.nakadi.client.scala.model.{Cursor, EventStreamBatch}


class EventCounterListener(val id: String) extends Listener[MeetingsEvent] {
  val log = LoggerFactory.getLogger(this.getClass)
  private var eventCount: AtomicLong = new AtomicLong(0);
  private var callerCount: AtomicLong = new AtomicLong(0);
  private var slept = false
  def onError(sourceUrl: String, error: Option[ClientError]): Unit = {
    println("Error %s %s".format(sourceUrl, error))
  }

  def onReceive(sourceUrl: String,
                cursor: Cursor,
                events: Seq[MeetingsEvent]): Unit = {
    eventCount.addAndGet(events.size.toLong)
    log.debug(s"Has a total of $eventCount events")
    log.info(s"Proccessed cursor $cursor")

  }
  def onSubscribed(endpoint: String, cursor: Option[Cursor]): Unit = {
    log.debug("########## onSubscribed ############")
    log.debug("Endpoint " + endpoint)
    log.debug("Cursor " + cursor)
    log.debug("#####################################")

  }
}


object SubscriptionIT {

  def main(args: Array[String]): Unit = {
    import org.zalando.nakadi.client.scala.model.ScalaJacksonJsonMarshaller._
    implicit def typeRef: TypeReference[EventStreamBatch[MeetingsEvent]] =
      new TypeReference[EventStreamBatch[MeetingsEvent]] {}

    val client: Client = ClientFactory.getScalaClient()
    val listener = new EventCounterListener("Test")

    client.subscribe(
      UUID.fromString("484cfdd9-fee9-4ec6-b1c6-9fcaebb8e322"),
      SubscriptionStreamParameters(),
      listener)

    Thread.sleep(10000)
  }

}
