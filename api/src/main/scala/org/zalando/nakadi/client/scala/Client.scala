package org.zalando.nakadi.client.scala

import scala.concurrent.Future

import org.zalando.nakadi.client.ClientError
import org.zalando.nakadi.client.Listener
import org.zalando.nakadi.client.Deserializer
import org.zalando.nakadi.client.Serializer
import org.zalando.nakadi.client.StreamParameters
import org.zalando.nakadi.client.model.EventEnrichmentStrategy
import org.zalando.nakadi.client.model.EventType
import org.zalando.nakadi.client.model.EventValidationStrategy
import org.zalando.nakadi.client.model.Metrics
import org.zalando.nakadi.client.model.Partition
import org.zalando.nakadi.client.model.PartitionStrategy




trait Client {

  /**
   * Retrieve monitoring metrics
   *
   * {{{
   * curl --request GET /metrics
   * }}}
   */
  def getMetrics()(implicit ser: Deserializer[Metrics]): Future[Either[ClientError, Option[Metrics]]]

  /**
   * Returns a list of all registered EventTypes.
   *
   * {{{
   * curl --request GET /event-types
   * }}}
   *
   */
  def getEventTypes()(implicit ser: Deserializer[Seq[EventType]]): Future[Either[ClientError, Option[Seq[EventType]]]]

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
  def createEventType(eventType: EventType)(implicit ser: Serializer[EventType]): Future[Option[ClientError]]

  /**
   * Returns the EventType identified by its name.
   * {{{
   * curl --request GET /event-types/{name}
   * }}}
   * @param name - Name of the EventType
   */
  def getEventType(name: String)(implicit ser: Deserializer[EventType]): Future[Either[ClientError, Option[EventType]]]
  /**
   * Updates the EventType identified by its name.
   * {{{
   * curl --request PUT -d @fileWithEventType /event-types/{name}
   * }}}
   * @param name - Name of the EventType
   * @param event - Event to update
   */
  def updateEventType(name: String, eventType: EventType)(implicit ser: Serializer[EventType]): Future[Option[ClientError]]
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
  def publishEvents[T](name: String, events: Seq[T])(implicit ser: Serializer[Seq[T]]): Future[Option[ClientError]]

  /**
   * Request a stream delivery for the specified partitions of the given EventType.
   * {{{
   * curl --request GET  /event-types/{name}/events
   * }}}
   * @param name - Name of the EventType
   *
   */
  def publishEvents[T](name: String, params: Option[StreamParameters])(implicit ser: Deserializer[T]): Future[Either[ClientError, Option[T]]]

  /**
   * List the partitions for the given EventType.
   * {{{
   * curl --request GET /event-types/{name}/partitions
   * }}}
   * @param name -  Name of the EventType
   */
  def getPartitions(name: String)(implicit ser: Deserializer[Seq[Partition]]): Future[Either[ClientError, Option[Seq[Partition]]]]

  /**
   * Returns the Partition for the given EventType.
   * {{{
   * curl --request GET /event-types/{name}/partitions/{partition}
   * }}}
   * @param name -  Name of the EventType
   * @param partition - Partition id for the given EventType
   */

  def getPartitionById(name: String, id: String)(implicit ser: Deserializer[Partition]): Future[Either[ClientError, Option[Partition]]]

  /**
   * Returns all of the validation strategies supported by this installation of Nakadi.
   *
   * {{{
   * curl --request GET /registry/validation-strategies
   * }}}
   */
  def getValidationStrategies()(implicit des: Deserializer[Seq[EventValidationStrategy.Value]]): Future[Either[ClientError, Option[Seq[EventValidationStrategy.Value]]]]

  /**
   * Returns all of the enrichment strategies supported by this installation of Nakadi.
   * {{{
   * curl --request GET /registry/enrichment-strategies
   * }}}
   */

  def getEnrichmentStrategies()(implicit des: Deserializer[Seq[EventEnrichmentStrategy.Value]]): Future[Either[ClientError, Option[Seq[EventEnrichmentStrategy.Value]]]]

  /**
   * Returns all of the partitioning strategies supported by this installation of Nakadi.
   * {{{
   * curl --request GET /registry/partitioning-strategies
   * }}}
   */
  def getPartitionStrategies()(implicit des: Deserializer[Seq[PartitionStrategy.Value]]): Future[Either[ClientError, Option[Seq[PartitionStrategy.Value]]]]

  /**
   * Shuts down the communication system of the client
   */

  def stop(): Future[Option[ClientError]]

  /**
   * Registers the subscription of a listener to start streaming events from a partition in non-blocking fashion.
   *
   * @eventType - Name of the EventType to listen for.
   * @parameters - Parameters for the streaming of events.
   * @listener - Listener to pass the event to when it is received.
   */
  def subscribe[T](eventType: String, parameters: StreamParameters, listener: Listener[T])(implicit ser: Deserializer[T]): Future[Option[ClientError]]
  /**
   * Removes the subscription of a listener, to stop streaming events from a partition.
   *
   * @eventType - Name of the EventType.
   * @listener - Listener to unsubscribe from the streaming events.
   */
  def unsubscribe[T](eventType: String, listener: Listener[T]): Future[Option[ClientError]]

}



