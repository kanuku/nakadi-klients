package org.zalando.nakadi.client

import scala.concurrent.Future

import org.zalando.nakadi.client.model.{ EventEnrichmentStrategy, EventType, EventValidationStrategy, Partition, PartitionResolutionStrategy }

import akka.actor.Terminated
import akka.http.scaladsl.marshalling.ToEntityMarshaller
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.FromEntityUnmarshaller

trait Client {
  import Client._

  /**
   * Retrieve monitoring metrics
   *
   * {{{
   * curl --request GET /metrics
   * }}}
   */
  def metrics(): Future[Either[ClientError, HttpResponse]]

  /**
   * Returns a list of all registered EventTypes.
   *
   * {{{
   * curl --request GET /event-types
   * }}}
   *
   */
  def eventTypes()(implicit marshaller: FromEntityUnmarshaller[List[EventType]]): Future[Either[ClientError, Option[List[EventType]]]]

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
  def newEventType(eventType: EventType)(implicit marshaller: ToEntityMarshaller[EventType]): Future[Option[ClientError]]

  /**
   * Returns the EventType identified by its name.
   * {{{
   * curl --request GET /event-types/{name}
   * }}}
   * @param name - Name of the EventType
   */
  def eventType(name: String)(implicit marshaller: FromEntityUnmarshaller[EventType]): Future[Either[ClientError, Option[EventType]]]
  /**
   * Updates the EventType identified by its name.
   * {{{
   * curl --request PUT -d @fileWithEventType /event-types/{name}
   * }}}
   * @param name - Name of the EventType
   * @param event - Event to update
   */
  def updateEventType(name: String, eventType: EventType)(implicit marshaller: ToEntityMarshaller[EventType]): Future[Option[ClientError]]
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
   * Creates a new Event for the given EventType.
   * {{{
   * curl --request POST -d @fileWithEvent /event-types/{name}/events
   * }}}
   * @param name - Name of the EventType
   * @param event - Event to create
   */
  def newEvent[T](name: String, event: T)(implicit marshaller: FromEntityUnmarshaller[T]): Future[Option[ClientError]]

  /**
   * Request a stream delivery for the specified partitions of the given EventType.
   * {{{
   * curl --request GET  /event-types/{name}/events
   * }}}
   * @param name - Name of the EventType
   *
   */
  def events[T](name: String)(implicit marshaller: FromEntityUnmarshaller[T]): Future[Either[ClientError, Option[T]]]

  /**
   * List the partitions for the given EventType.
   * {{{
   * curl --request GET /event-types/{name}/partitions
   * }}}
   * @param name -  Name of the EventType
   */
  def partitions(name: String)(implicit marshaller: FromEntityUnmarshaller[Partition]): Future[Either[ClientError, Option[Partition]]]

  /**
   * Returns the Partition for the given EventType.
   * {{{
   * curl --request GET /event-types/{name}/partitions/{partition}
   * }}}
   * @param name -  Name of the EventType
   * @param partition - Name of the partition
   */

  def partitionByName(name: String, partition: String)(implicit marshaller: FromEntityUnmarshaller[Partition]): Future[Either[ClientError, Option[Partition]]]

  /**
   * Returns all of the validation strategies supported by this installation of Nakadi.
   *
   * {{{
   * curl --request GET /registry/validation-strategies
   * }}}
   */
  def validationStrategies()(implicit marshaller: FromEntityUnmarshaller[EventValidationStrategy]): Future[Either[ClientError, Option[EventValidationStrategy]]]

  /**
   * Returns all of the enrichment strategies supported by this installation of Nakadi.
   * {{{
   * curl --request GET /registry/enrichment-strategies
   * }}}
   */

  def enrichmentStrategies()(implicit marshaller: FromEntityUnmarshaller[EventEnrichmentStrategy]): Future[Either[ClientError, Option[EventEnrichmentStrategy]]]

  /**
   * Returns all of the partitioning strategies supported by this installation of Nakadi.
   * {{{
   * curl --request GET /registry/partitioning-strategies
   * }}}
   */
  def partitionStrategies()(implicit marshaller: FromEntityUnmarshaller[List[PartitionResolutionStrategy]]): Future[Either[ClientError, Option[List[PartitionResolutionStrategy]]]]

  /**
   * Shuts down the communication system of the client
   */

  def stop(): Future[Terminated]

}

object Client {
  case class ClientError(msg: String, status: Option[Int])
}

