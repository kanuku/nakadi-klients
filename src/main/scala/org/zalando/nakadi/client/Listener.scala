package org.zalando.nakadi.client


trait Listener {
  def onReceive(topic: String, partition: String, cursor: Cursor, event: Event)
  def onConnectionClosed(topic: String, partition: String, lastCursor: Cursor)
}
