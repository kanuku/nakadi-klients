package org.zalando.nakadi.client.scala

import org.zalando.nakadi.client.ClientError
import org.zalando.nakadi.client.scala.model.Cursor
import org.zalando.nakadi.client.scala.model.Event




trait Listener[T <: Event] {
  def id: String
  def onSubscribed(): Unit
  def onUnsubscribed(): Unit
  def onReceive(sourceUrl: String, cursor: Cursor, event: Seq[T]): Unit
  def onError(sourceUrl: String, cursor: Cursor, error: ClientError): Unit
}