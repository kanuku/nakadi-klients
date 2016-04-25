package org.zalando.nakadi.client

import scala.concurrent.Future
import org.zalando.nakadi.client.model._
import akka.actor.Terminated
import akka.http.scaladsl.model.HttpResponse

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

trait Client {

  /**
   * Retrieve monitoring metrics
   *
   * {{{
   * curl --request GET /metrics
   * }}}
   */
  def metrics()(implicit ser: NakadiDeserializer[Metrics]): Future[Either[ClientError, Option[Metrics]]]

  /**
   * Returns a list of all registered EventTypes.
   *
   * {{{
   * curl --request GET /event-types
   * }}}
   *
   */
  def eventTypes()(implicit ser: NakadiDeserializer[Seq[EventType]]): Future[Either[ClientError, Option[Seq[EventType]]]]

  /**
   * Creates a new EventType.
   *
   * {{{
   * curl --request POST -d @fileWithEventType /event-types
   * }}}
   *
   * @param event - The EventType to create.
   *
   */
  def newEventType(eventType: EventType)(implicit ser: NakadiSerializer[EventType]): Future[Option[ClientError]]

  /**
   * Returns the EventType identified by its name.
   * {{{
   * curl --request GET /event-types/{name}
   * }}}
   * @param name - Name of the EventType
   */
  def eventType(name: String)(implicit ser: NakadiDeserializer[EventType]): Future[Either[ClientError, Option[EventType]]]
  /**
   * Updates the EventType identified by its name.
   * {{{
   * curl --request PUT -d @fileWithEventType /event-types/{name}
   * }}}
   * @param name - Name of the EventType
   * @param event - Event to update
   */
  def updateEventType(name: String, eventType: EventType)(implicit ser: NakadiSerializer[EventType]): Future[Option[ClientError]]
  /**
   * Deletes an EventType identified by its name.
   *
   * {{{
   * curl --request DELETE /event-types/{name}
   * }}}
   *
   * @param name - Name of the EventType
   */
  def deleteEventType(name: String): Future[Option[ClientError]]

  /**
   * Creates a new batch of Events for the given EventType.
   * {{{
   * curl --request POST -d @fileWithEvent /event-types/{name}/events
   * }}}
   * @param name - Name of the EventType
   * @param event - Event to create
   */
  def newEvents[T](name: String, events: Seq[T])(implicit ser: NakadiSerializer[Seq[T]]): Future[Option[ClientError]]

  /**
   * Request a stream delivery for the specified partitions of the given EventType.
   * {{{
   * curl --request GET  /event-types/{name}/events
   * }}}
   * @param name - Name of the EventType
   *
   */
  def events[T](name: String, params: Option[StreamParameters])(implicit ser: NakadiDeserializer[T]): Future[Either[ClientError, Option[T]]]

  /**
   * List the partitions for the given EventType.
   * {{{
   * curl --request GET /event-types/{name}/partitions
   * }}}
   * @param name -  Name of the EventType
   */
  def partitions(name: String)(implicit ser: NakadiDeserializer[Seq[Partition]]): Future[Either[ClientError, Option[Seq[Partition]]]]

  /**
   * Returns the Partition for the given EventType.
   * {{{
   * curl --request GET /event-types/{name}/partitions/{partition}
   * }}}
   * @param name -  Name of the EventType
   * @param partition - Partition id for the given EventType
   */

  def partitionById(name: String, id: String)(implicit ser: NakadiDeserializer[Partition]): Future[Either[ClientError, Option[Partition]]]

  /**
   * Returns all of the validation strategies supported by this installation of Nakadi.
   *
   * {{{
   * curl --request GET /registry/validation-strategies
   * }}}
   */
  def validationStrategies()(implicit des: NakadiDeserializer[Seq[EventValidationStrategy.Value]]): Future[Either[ClientError, Option[Seq[EventValidationStrategy.Value]]]]

  /**
   * Returns all of the enrichment strategies supported by this installation of Nakadi.
   * {{{
   * curl --request GET /registry/enrichment-strategies
   * }}}
   */

  def enrichmentStrategies()(implicit des: NakadiDeserializer[Seq[EventEnrichmentStrategy.Value]]): Future[Either[ClientError, Option[Seq[EventEnrichmentStrategy.Value]]]]

  /**
   * Returns all of the partitioning strategies supported by this installation of Nakadi.
   * {{{
   * curl --request GET /registry/partitioning-strategies
   * }}}
   */
  def partitionStrategies()(implicit des: NakadiDeserializer[Seq[PartitionStrategy.Value]]): Future[Either[ClientError, Option[Seq[PartitionStrategy.Value]]]]

  /**
   * Shuts down the communication system of the client
   */

  def stop(): Future[Terminated]

  /**
   * Registers the subscription of a listener to start streaming events from a partition in non-blocking fashion.
   *
   * @eventType - Name of the EventType to listen for.
   * @parameters - Parameters for the streaming of events.
   * @listener - Listener to pass the event to when it is received.
   */
  def subscribe[T](eventType: String, parameters: StreamParameters, listener: Listener[T])(implicit ser: NakadiDeserializer[T]): Future[Option[ClientError]]
  /**
   * Removes the subscription of a listener, to stop streaming events from a partition.
   *
   * @eventType - Name of the EventType.
   * @listener - Listener to unsubscribe from the streaming events.
   */
  def unsubscribe[T](eventType: String, listener: Listener[T]): Future[Option[ClientError]]

}



