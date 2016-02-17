package org.zalando.nakadi.client

import scala.collection.JavaConversions._

case class Cursor(partition: String, offset: String)

case class Event(eventType: String, orderingKey: String, metadata: Map[String, Any], body: AnyRef) {
  def this(eventType: String, orderingKey: String, metadata: java.util.Map[String, Any], body: AnyRef) =
    this(eventType, orderingKey, metadata.toMap, body)
}

case class Topic(name: String)

case class TopologyItem (clientId: String, partitions: List[String])  {
  def this(clientId: String, partitions: java.util.List[String]) =
    this(clientId, partitions.toList)
}

case class TopicPartition(partitionId: String, oldestAvailableOffset: String, newestAvailableOffset: String)

case class SimpleStreamEvent(cursor: Cursor, events: List[Event], topology: List[TopologyItem]) {
  def this(cursor: Cursor, events: java.util.List[Event], topology: java.util.List[TopologyItem]) =
    this(cursor, events.toList, topology.toList)
}