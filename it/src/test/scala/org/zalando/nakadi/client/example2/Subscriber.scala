package org.zalando.nakadi.client.example2

import com.fasterxml
import org.zalando
import org.zalando
import org.zalando
import scala.concurrent.Future
import org.zalando.nakadi.client.model.JacksonJsonMarshaller
import org.zalando.nakadi.client.ClientFactory
import org.zalando.nakadi.client.StreamParameters
import org.zalando.nakadi.client.Listener
import org.zalando.nakadi.client.ClientError
import com.fasterxml.jackson.core.`type`.TypeReference
import org.zalando.nakadi.client.model.Cursor
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

case class MyEventExample(orderNumber: String)
object Subscriber extends App {
  val a = new A()
  //  a.startListening()
  //  a.sendEvents()
  a.printPartitions()
}

class A extends ClientFactory with JacksonJsonMarshaller {
  val eventType = "test-client-integration-event-1936085527-148383828851369665"
  implicit def myEventExampleTR: TypeReference[MyEventExample] = new TypeReference[MyEventExample] {}
  def startListening() = {

    val listener = new Listener[MyEventExample] {
      def id: String = "test"
      def onError(sourceUrl: String, cursor: Cursor, error: ClientError): Unit = {
        println("YOOOOOOOOOOOOOO ")
      }
      def onSubscribed(): Unit = ???
      def onUnsubscribed(): Unit = ???
      def onReceive(sourceUrl: String, cursor: Cursor, event: MyEventExample): Unit = ???
    }
    val url = "/event-types/test-client-integration-event-1936085527-148383828851369665/events"
    val c = Cursor(0, Some(1))
    val params = new StreamParameters(cursor = Some(c))
    client.subscribe(eventType, params, listener)
  }

  def printPartitions() = {
    val result = Await.result(client.partitions(eventType), 5.seconds)
    println("###########################")
    println("partitions" + result)
    println("###########################")

  }
  def sendEvents() = {
    val events = for {
      a <- 0 to 1
    } yield MyEventExample("order-" + a)
    client.newEvents(eventType, events)
  }
}