package org.zalando.nakadi.client


import play.api.libs.json._
import play.api.libs.functional.syntax.{toContraFunctorOps, functionalCanBuildApplicative, toFunctionalBuilderOps, unlift}

case class Cursor(partition: String, offset: String)
case class Event(eventType: String, orderingKey: String, metadata: Map[String, Any], body: AnyRef)
case class Topic(name: String)
case class TopologyItem (clientId: String, partitions: List[String])
case class TopicPartition(partitionId: String, oldestAvailableOffset: String, newestAvailableOffset: String)
case class SimpleStreamEvent(cursor: Cursor, events: List[Event], topology: List[TopologyItem])

/*
object Formatter{


  implicit val cursorReader: Reads[Cursor] = (
      (__ \ "partition").read[String] and
      (__ \ "offset").read[String]
    )(Cursor.apply _)

  implicit val cursorWriter : Writes[Cursor] = (
      (__ \ "partition").write[String] and
      (__ \ "offset").write[String]
    )(unlift(Cursor.unapply))

  implicit val cursorFormat = Format[Cursor](cursorReader, cursorWriter)

  //--------

  implicit val eventReader: Reads[Event] = (
     (__ \ "event_type").read[String] and
     (__ \ "ordering_key").read[String] and
     (__ \ "metadata").read[Map[String, Any]] and
     (__ \ "body").read[AnyRef]
    )(Event.apply _)


  implicit val eventWriter: Writes[Event] = (
      (__ \ "event_type").write[String] and
      (__ \ "ordering_key").write[String] and
      (__ \ "metadata").write[Map[String, Any]] and
      (__ \ "body").write[AnyRef]
    )(unlift(Event.unapply _))

  implicit val eventFormat = Format(eventReader, eventWriter)

  //--------

  implicit val topicReader: Reads[Topic] = (JsPath \ "name").read[String].map{ name => Topic(name) }

  implicit val topicWriter: Writes[Topic] = (__ \ "name").write[String].contramap((topic: Topic) => topic.name)

  implicit val topicFormat = Format(topicReader, topicWriter)

  //--------

  implicit val topologyItemReader: Reads[TopologyItem] = (
      (__ \ "client_id").read[String] and
      (__ \ "partitions").read[List[String]]
    )(TopologyItem.apply _)

  implicit val topologyItemWriter: Writes[TopologyItem] = (
      (__ \ "client_id").write[String] and
      (__ \ "partitions").write[List[String]]
    )(unlift(TopologyItem.unapply _))

  implicit val topologyItemFormat = Format(topologyItemReader, topologyItemWriter)

  //--------

  implicit val topicPartitionReader: Reads[TopicPartition] = (
      (__ \ "partition_id").read[String] and
      (__ \ "oldest_available_offset").read[String] and
      (__ \ "newest_available_offset").read[String]
    )(TopicPartition.apply _)

  implicit val topicPartitionWriter: Writes[TopicPartition] = (
    (__ \ "partition_id").write[String] and
    (__ \ "oldest_available_offset").write[String] and
    (__ \ "newest_available_offset").write[String]
  )(unlift(TopicPartition.unapply _))

  implicit val topicPartitionFormat = Format(topicPartitionReader, topicPartitionWriter)

  //--------

  implicit val simpleStreamEventReader: Reads[SimpleStreamEvent] = (
      (__ \ "cursor").read[Cursor] and
      (__ \ "events").read[List[Event]] and
      (__ \ "topology").read[List[TopologyItem]]
    )(SimpleStreamEvent.apply _)

  implicit val simpleStreamEventWriter: Writes[SimpleStreamEvent] = (
      (__ \ "cursor").write[Cursor] and
      (__ \ "events").write[List[Event]] and
      (__ \ "topology").write[List[TopologyItem]]
    )(unlift(SimpleStreamEvent.unapply _))

  implicit val simpleStreamEventFormat = Format(simpleStreamEventReader, simpleStreamEventWriter)

}
*/