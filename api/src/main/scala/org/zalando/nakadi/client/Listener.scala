package org.zalando.nakadi.client

import org.zalando.nakadi.client.model.Cursor

case class ClientError(msg: String, status: Option[Int])


case class StreamParameters(cursor: Option[Cursor] = None, //
                            batchLimit: Option[Integer] = None,
                            streamLimit: Option[Integer] = None,
                            batchFlushTimeout: Option[Integer] = None,
                            streamTimeout: Option[Integer] = None,
                            streamKeepAliveLimit: Option[Integer] = None,
                            flowId: Option[String] = None) {
}

trait Listener[T] {
  def id: String
  def onSubscribed(): Unit
  def onUnsubscribed(): Unit
  def onReceive(sourceUrl: String, cursor: Cursor, event: T): Unit
  def onError(sourceUrl: String, cursor: Cursor, error: ClientError): Unit
}