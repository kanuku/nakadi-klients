package org.zalando.nakadi.client

import scala.concurrent.Future
import org.zalando.nakadi.client.model.{ EventEnrichmentStrategy, EventType, EventValidationStrategy, Partition, PartitionResolutionStrategy }
import akka.actor.Terminated
import akka.http.scaladsl.model.HttpResponse
import org.zalando.nakadi.client.model.Metrics

trait Client {
  import Client._

  /**
   * Retrieve monitoring metrics
   *
   * {{{
   * curl --request GET /metrics
   * }}}
   */
  def metrics()(implicit ser: Deserializer[Metrics]): Future[Either[ClientError, Option[Metrics]]]

  /**
   * Returns a list of all registered EventTypes.
   *
   * {{{
   * curl --request GET /event-types
   * }}}
   *
   */
  def eventTypes()(implicit ser: Deserializer[Seq[EventType]]): Future[Either[ClientError, Option[Seq[EventType]]]]

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
  def newEventType(eventType: EventType)(implicit ser: Serializer[EventType]): Future[Option[ClientError]]

  /**
   * Returns the EventType identified by its name.
   * {{{
   * curl --request GET /event-types/{name}
   * }}}
   * @param name - Name of the EventType
   */
  def eventType(name: String)(implicit ser: Deserializer[EventType]): Future[Either[ClientError, Option[EventType]]]
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
  def newEvents[T](name: String, events: Seq[T])(implicit ser: Serializer[Seq[T]]): Future[Option[ClientError]]

  /**
   * Request a stream delivery for the specified partitions of the given EventType.
   * {{{
   * curl --request GET  /event-types/{name}/events
   * }}}
   * @param name - Name of the EventType
   *
   */
  def events[T](name: String)(implicit ser: Deserializer[T]): Future[Either[ClientError, Option[T]]]

  /**
   * List the partitions for the given EventType.
   * {{{
   * curl --request GET /event-types/{name}/partitions
   * }}}
   * @param name -  Name of the EventType
   */
  def partitions(name: String)(implicit ser: Deserializer[Partition]): Future[Either[ClientError, Option[Partition]]]

  /**
   * Returns the Partition for the given EventType.
   * {{{
   * curl --request GET /event-types/{name}/partitions/{partition}
   * }}}
   * @param name -  Name of the EventType
   * @param partition - Partition id for the given EventType
   */

  def partitionById(name: String, id: String)(implicit ser: Deserializer[Partition]): Future[Either[ClientError, Option[Partition]]]

  /**
   * Returns all of the validation strategies supported by this installation of Nakadi.
   *
   * {{{
   * curl --request GET /registry/validation-strategies
   * }}}
   */
  def validationStrategies()(implicit des: Deserializer[Seq[EventValidationStrategy]]): Future[Either[ClientError, Option[Seq[EventValidationStrategy]]]]

  /**
   * Returns all of the enrichment strategies supported by this installation of Nakadi.
   * {{{
   * curl --request GET /registry/enrichment-strategies
   * }}}
   */

  def enrichmentStrategies()(implicit des: Deserializer[Seq[EventEnrichmentStrategy]]): Future[Either[ClientError, Option[Seq[EventEnrichmentStrategy]]]]

  /**
   * Returns all of the partitioning strategies supported by this installation of Nakadi.
   * {{{
   * curl --request GET /registry/partitioning-strategies
   * }}}
   */
  def partitionStrategies()(implicit des: Deserializer[Seq[PartitionResolutionStrategy]]): Future[Either[ClientError, Option[Seq[PartitionResolutionStrategy]]]]

  /**
   * Shuts down the communication system of the client
   */

  def stop(): Future[Terminated]

}

object Client {
  case class ClientError(msg: String, status: Option[Int])
}

