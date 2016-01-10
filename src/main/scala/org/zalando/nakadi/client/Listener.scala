package org.zalando.nakadi.client


trait Listener {
  def onReceive(topic: String, partition: String, cursor: Cursor, event: Event)
  def onConnectionOpened(topic: String, partition: String)
  def onConnectionClosed(topic: String, partition: String, lastCursor: Option[Cursor])
}
