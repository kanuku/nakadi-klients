package org.zalando.nakadi.client.scala

import org.zalando.nakadi.client.scala.model.Cursor
import org.zalando.nakadi.client.scala.model.Event




trait Listener[T] {
  def id: String
  def onReceive(eventUrl: String, cursor: Cursor, events: Seq[T]): Unit
  def onError(eventUrl: String, error: Option[ClientError]): Unit
}