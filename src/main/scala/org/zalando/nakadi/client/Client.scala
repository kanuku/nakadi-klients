package org.zalando.nakadi.client

import org.zalando.nakadi.client.domain.Event

import scala.concurrent.Future


trait Client {
  /**
   * Gets monitoring metrics.
   * NOTE: metrics format is not defined / fixed
   *
   * @return immutable map of metrics data (value can be another Map again)
   */
  def getMetrics: Future[Map[String, AnyRef]]

  /**
   * Post a single event to the given topic.  Partition selection is done using the defined partition resolution.
   * The partition resolution strategy is defined per topic and is managed by event store (currently resolved from
   * hash over Event.orderingKey).
   * @param topic  target topic
   * @param event  event to be posted
   */
  def postEvent(topic: String, event: Event, callback: => Unit): Unit
}
