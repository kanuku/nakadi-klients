package org.zalando.nakadi.client.examples.scala

import org.zalando.nakadi.client.scala.ClientFactory
import org.zalando.nakadi.client.utils.ClientBuilder
import org.zalando.nakadi.client.scala.Client
import org.zalando.nakadi.client.scala._
import org.zalando.nakadi.client.scala.model._
import EventCreationExample._
import com.fasterxml.jackson.core.`type`.TypeReference
import java.util.concurrent.atomic.AtomicLong

/**
 * Your listener will have to implement the necessary
 */
class EventCounterListener(val id: String) extends Listener[MeetingsEvent] {
  private var eventCount: AtomicLong = new AtomicLong(0);

  def onError(sourceUrl: String, cursor: Cursor, error: ClientError): Unit = {
    println("Error %s %s %s".format(sourceUrl,cursor,error))
  }

  def onReceive(sourceUrl: String, cursor: Cursor, events: Seq[MeetingsEvent]): Unit = {
    eventCount.addAndGet(events.size.toLong)
    println("#####################################")
    println(s"Received " + events.size.toLong)
    println(s"Has a total of $eventCount events")
    println("#####################################")

  }
}

object EventListenerExample extends App {

  /**
   * Create our client
   */
  val client: Client = ClientBuilder()
    .withHost(ClientFactory.host())
    .withSecuredConnection(true) //s
    .withVerifiedSslCertificate(false) //s
    .withTokenProvider(ClientFactory.OAuth2Token()) //
    //    .build();
    .build()

  /**
   * Initialize our Listener
   */
  val listener = new EventCounterListener("Test")

  /**
   * Create the Parameters with the cursor.
   */

  val cursor = Cursor(0, 4)

  val parameters = new StreamParameters(
    cursor =  Some(cursor) //
    , batchLimit = Some(100) //  Maximum number of `Event`s in each chunk (and therefore per partition) of the stream.  
//    , streamLimit = Some(2) // Maximum number of `Event`s to stream (over all partitions being streamed in this
    //connection).
//    , batchFlushTimeout = Some(15) // Maximum time in seconds to wait for the flushing of each chunk (per partition).
    //        ,streamKeepAliveLimit=Some(4)
//    , streamTimeout = Some(30)
    )

  /**
   * Create TypeReference for the JacksonObjectMapper
   */

  implicit def typeRef: TypeReference[EventStreamBatch[MeetingsEvent]] = new TypeReference[EventStreamBatch[MeetingsEvent]] {}
  import org.zalando.nakadi.client.scala.model.JacksonJsonMarshaller._

  val eventTypeName = "MeetingsEvent-example-E"
  val result = client.subscribe(eventTypeName, parameters, listener)

}