package org.zalando.nakadi.client.scala

import scala.concurrent.Future
import org.zalando.nakadi.client.Deserializer
import org.zalando.nakadi.client.Serializer
import org.zalando.nakadi.client.scala.model._
import org.zalando.nakadi.client._




trait Client {

  /**
   * Retrieve monitoring metrics
   *
   * {{{
   * curl --request GET /metrics
   * }}}
   */
  def getMetrics()(implicit des: Deserializer[Metrics]): Future[Either[ClientError, Option[Metrics]]]

  /**
   * Returns a list of all registered EventTypes.
   *
   * {{{
   * curl --request GET /event-types
   * }}}
   *
   */
  def getEventTypes()(implicit des: Deserializer[Seq[EventType]]): Future[Either[ClientError, Option[Seq[EventType]]]]

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
   * @param eventTypeName - Name of the EventType
   */
  def getEventType(eventTypeName: String)(implicit des: Deserializer[EventType]): Future[Either[ClientError, Option[EventType]]]
  /**
   * Updates the EventType identified by its name.
   * {{{
   * curl --request PUT -d @fileWithEventType /event-types/{name}
   * }}}
   * @param eventTypeName - Name of the EventType
   * @param event - Event to update
   */
  def updateEventType(eventTypeName: String, eventType: EventType)(implicit ser: Serializer[EventType]): Future[Option[ClientError]]
  /**
   * Deletes an EventType identified by its name.
   *
   * {{{
   * curl --request DELETE /event-types/{name}
   * }}}
   *
   * @param eventTypeName - Name of the EventType
   */
  def deleteEventType(eventTypeName: String): Future[Option[ClientError]]

  /**
   * Publishes multiple Events for the given EventType.
   * {{{
   * curl --request POST -d @fileWithEvent /event-types/{name}/events
   * }}}
   * @param eventTypeName - Name of the EventType
   * @param event - Event to publish
   */
  def publishEvents[T <: Event](eventTypeName: String, events: Seq[T])(implicit ser: Serializer[Seq[T]]): Future[Option[ClientError]]

  /**
   * Publishes a single Events for the given EventType.
   * {{{
   * curl --request POST -d @fileWithEvent /event-types/{name}/events
   * }}}
   * @param eventTypeName - Name of the EventType
   * @param event - Event to publish
   *
   */
  def publishEvent[T <: Event](eventTypeName: String, event: T)(implicit ser: Serializer[T]): Future[Option[ClientError]]

  /**
   * List the partitions for the given EventType.
   * {{{
   * curl --request GET /event-types/{name}/partitions
   * }}}
   * @param eventTypeName -  Name of the EventType
   */
  def getPartitions(eventTypeName: String)(implicit des: Deserializer[Seq[Partition]]): Future[Either[ClientError, Option[Seq[Partition]]]]

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
  def getPartitioningStrategies()(implicit des: Deserializer[Seq[PartitionStrategy.Value]]): Future[Either[ClientError, Option[Seq[PartitionStrategy.Value]]]]

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
  def subscribe[T <: Event](eventTypeName: String, parameters: StreamParameters, listener: Listener[T])(implicit des: Deserializer[T]): Future[Option[ClientError]]
  /**
   * Removes the subscription of a listener, to stop streaming events from a partition.
   *
   * @eventType - Name of the EventType.
   * @listener - Listener to unsubscribe from the streaming events.
   */
  def unsubscribe[T <: Event](eventTypeName: String, listener: Listener[T]): Future[Option[ClientError]]

}



