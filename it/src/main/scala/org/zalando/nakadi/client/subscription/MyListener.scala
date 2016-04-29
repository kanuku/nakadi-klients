package org.zalando.nakadi.client.subscription

import org.zalando.nakadi.client.scala.model.Cursor
import org.zalando.nakadi.client.scala.Listener
import org.zalando.nakadi.client.scala.ClientError

class MyListener extends Listener[MyEventExample] {
  def id: String = "test"
  def onError(sourceUrl: String, cursor: Cursor, error: ClientError): Unit = {
    println("YOOOOOOOOOOOOOO ")
  }
  def onSubscribed(): Unit = ???
  def onUnsubscribed(): Unit = ???
  def onReceive(sourceUrl: String, cursor: Cursor, event: Seq[MyEventExample]): Unit = ???

}