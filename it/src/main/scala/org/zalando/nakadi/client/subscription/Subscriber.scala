package org.zalando.nakadi.client.subscription

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

import org.zalando.nakadi.client.scala.ClientFactory
import org.zalando.nakadi.client.scala.StreamParameters
import org.zalando.nakadi.client.scala.model.Cursor
import org.zalando.nakadi.client.scala.model.JacksonJsonMarshaller

import com.fasterxml.jackson.core.`type`.TypeReference


object Subscriber extends App {
  val a = new Subscriber()
  a.startListening()
  //  a.printPartitions()
  //  a.printEventTypes()
  //  a.sendEvents(30)
}

class Subscriber {
  import ClientFactory._
  import JacksonJsonMarshaller._
  val eventType = "test-client-integration-event-1936085527-148383828851369665"
  implicit def myEventExampleTR: TypeReference[MyEventExample] = new TypeReference[MyEventExample] {}
  def startListening() = {

    val listener = new MyListener
    val url = "/event-types/test-client-integration-event-1936085527-148383828851369665/events"
    val cr = Cursor(0, 170000)
    val params = new StreamParameters(
      cursor = Some(cr), batchLimit = Some(500) //        ,streamLimit=Some(10)
//              ,streamTimeout=Some(10)
      //        ,streamKeepAliveLimit =Some(10)
      )
//    client.subscribe(eventType, params, listener)
  }

  def printPartitions() = {
    val result = Await.result(client.getPartitions(eventType), 5.seconds)
    print("partitions", result)
  }

  def printEventTypes() = {
    val result = Await.result(client.getEventTypes(), 5.seconds)
    print("eventTypes", result)
  }

  def print(msg: String, obj: Any) = {
    println("###########################")
    println(s"$msg - " + obj)
    println("###########################")
  }
  def sendEvents(in: Int) = {
    val events = for {
      a <- 1 to in
    } yield MyEventExample("order-" + a)
    client.publishEvents(eventType, events)
  }
}

 