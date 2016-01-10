package org.zalando.nakadi.client

import java.net.URI

import com.fasterxml.jackson.databind.ObjectMapper
import play.api.libs.json._
import play.api.libs.ws.ning.NingWSClient
import play.api.libs.ws._

import scala.collection.immutable.HashMap
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global



// TODO create builder + make this class package protected
class KlientImpl(val endpoint: URI, val tokenProvider: () => String) extends Klient{
  checkNotNull(endpoint, "endpoint must not be null")
  checkNotNull(tokenProvider, "tokenProvider must not be null")
  val wsClient = NingWSClient()

  def checkNotNull(subject: Any, message: String) = if(Option(subject) == None) throw new IllegalArgumentException(message)

  /**
   * Gets monitoring metrics.
   * NOTE: metrics format is not defined / fixed
   *
   * @return immutable map of metrics data (value can be another Map again)
   */
  override def getMetrics: Future[Either[String, Map[String, AnyRef]]] =
    createDefaultRequest(endpoint.toString + URI_METRICS)
              .get()
              .map{response =>
                  if(response.status < 200 || response.status > 299)
                    Left(response.status + " - " + response.body)
                  else
                    Right(Json.parse(response.body).as[Map[String, JsValue]].map{f => (f._1, convert(f._2)) })
              }
    
    private def createDefaultRequest(url:String):  WSRequest =
      wsClient.url(url)
        .withHeaders (("Authorization", "Bearer " + tokenProvider.apply()) ,
            ("Content-Type", "application/json"))



    private def convert( input: JsValue): AnyRef = input match {
        case value: JsObject => value.value.map(f => convert(f._2))
        case everythingElse => everythingElse.toString()
    }


  /**
   * Get partition information of a given topic
   *
   * @param topic   target topic
   * @return immutable list of topic's partitions information
   */
  override def getPartitions(topic: String)(implicit reader:Reads[List[TopicPartition]]): Future[Either[String, List[TopicPartition]]] = {
    checkNotNull(topic, "topic must not be null")
    performDefaultRequest(String.format(URI_PARTITIONS,topic))
  }

  private def evaluateResponse[T](response: WSResponse)(implicit reader:Reads[T]):Either[String,T] = {
    if(response.status < 200 || response.status > 299)
      Left(response.status + " - " + response.body)
    else
    Json.fromJson[T](Json.parse(response.body)) match {
      case value: JsSuccess[T] => Right(value.get)
      case JsError(error) => Left(s"Failed to parse because: $error")
    }
  }


  def performDefaultRequest[T](uriPart: String)(implicit reader:Reads[T]): Future[Either[String, T]] =
    createDefaultRequest(endpoint.toString + uriPart)
            .get()
            .map(evaluateResponse(_))




  /**
   * Get specific partition
   *
   * @param topic  topic where the partition is located
   * @param partitionId  id of the target partition
   * @return Either error message or TopicPartition in case of success
   */
  override def getPartition(topic: String, partitionId: String)(implicit reader:Reads[TopicPartition]): Future[Either[String, TopicPartition]] = {
    checkNotNull(topic, "topic must not be null")
    checkNotNull(partitionId, "partitionId must not be null")
    performDefaultRequest(String.format(URI_PARTITION, topic, partitionId))
  }


  /**
   * Lists all known `Topics` in Event Store.
   *
   * @return immutable list of known topics
   */
  def getTopics()(implicit reader: Reads[List[Topic]]): Future[Either[String, List[Topic]]] = performDefaultRequest(URI_TOPICS)


  /**
   * Blocking subscription to events of specified topic and partition.
   * (batchLimit is set to 1, batch flush timeout to 1,  and streamLimit to 0 -> infinite streaming receiving 1 event per poll)
   *
   * @param parameters listen parameters
   * @param listener  listener consuming all received events
   * @return Either error message or connection was closed and reconnect is set to false
   */
  override def listenForEvents(topic: String, partitionId: String, parameters: ListenParameters, listener: (Cursor, Event) => Unit, autoReconnect: Boolean): Future[Either[String, _]] = ???


  /**
   * Non-blocking subscription to a topic requires a `EventListener` implementation. The event listener must be thread-safe because
   * the listener listens to all partitions of a topic (one thread each).
   *
   * @param parameters listen parameters
   * @param listener  listener consuming all received events
   * @return {Future} instance of listener threads
   */
  override def subscribeToTopic(topic: String, partitionId: String, parameters: ListenParameters, listener: (Cursor, Event) => Unit, autoReconnect: Boolean): Future[Either[String, _]] = ???

   /**
   * Post a single event to the given topic.  Partition selection is done using the defined partition resolution.
   * The partition resolution strategy is defined per topic and is managed by event store (currently resolved from
   * hash over Event.orderingKey).
   * @param topic  target topic
   * @param event  event to be posted
   * @return Option representing the error message or None in case of success
   */
  override def postEvent(topic: String, event: Event): Future[Option[String]] = ???




  /**
   * Post event to specific partition.
   * NOTE: not implemented by Nakadi yet
   *
   * @param topic  topic where the partition is located
   * @param partitionId  id of the target partition
   * @param event event to be posted
   * @return Option representing the error message or None in case of success
   */
  override def postEventToPartition(topic: String, partitionId: String, event: Event): Future[Option[String]] = ???
}
