package org.zalando.nakadi.client.scala

import org.zalando.nakadi.client.scala.model.Cursor
import org.zalando.nakadi.client.scala.model.Event

trait Listener[T] {
  def id: String
  def onReceive(endpoint: String, cursor: Cursor, events: Seq[T]): Unit
  def onSubscribed(endpoint: String, cursor: Option[Cursor]): Unit
  def onError(endpoint: String, error: Option[ClientError]): Unit
}
