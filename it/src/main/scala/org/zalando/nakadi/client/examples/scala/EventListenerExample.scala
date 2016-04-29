package org.zalando.nakadi.client.examples.scala

import org.zalando.nakadi.client.scala.ClientFactory
import org.zalando.nakadi.client.utils.ClientBuilder
import org.zalando.nakadi.client.scala.Client
import org.zalando.nakadi.client.scala._
import org.zalando.nakadi.client.scala.model._
import EventCreationExample._
import com.fasterxml.jackson.core.`type`.TypeReference

/**
 * Your listener will have to implement the necessary
 */
class MeetingsListener() extends Listener[MeetingsEvent] {

  def id: String = "test"
  def onError(sourceUrl: String, cursor: Cursor, error: ClientError): Unit = {
    println("YOOOOOOOOOOOOOO ")
  }
  def onSubscribed(): Unit = ???
  def onUnsubscribed(): Unit = ???
  def onReceive(sourceUrl: String, cursor: Cursor, event: Seq[MeetingsEvent]): Unit = ???
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
  val listener = new MeetingsListener()

  /**
   * Create the Parameters with the cursor.
   */
  
   val cursor = Cursor(0, 0)    
   
    val parameters = new StreamParameters( 
        cursor = Some(cursor) 
        ,batchLimit = Some(30)
//        ,batchFlushTimeout = Some(1)
//        ,streamKeepAliveLimit=Some(4)
//        ,streamTimeout=Some(1)
        )

  /**
   * Create TypeReference for the JacksonObjectMapper
   */

  implicit def typeRef: TypeReference[MeetingsEvent] = new TypeReference[MeetingsEvent] {}
  import org.zalando.nakadi.client.scala.model.JacksonJsonMarshaller._
  
  val eventTypeName = "MeetingsEvent-example-E"
  val result = client.subscribe(eventTypeName, parameters, listener)

}