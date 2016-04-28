package org.zalando.nakadi.client.scala

import org.zalando.nakadi.client.scala.model.Event
import org.zalando.nakadi.client.ClientError
import org.zalando.nakadi.client.Cursor




trait Listener[T <: Event] {
  def id: String
  def onSubscribed(): Unit
  def onUnsubscribed(): Unit
  def onReceive(sourceUrl: String, cursor: Cursor, event: Seq[T]): Unit
  def onError(sourceUrl: String, cursor: Cursor, error: ClientError): Unit
}