package org.zalando.nakadi.client.scala.test.factory

import org.zalando.nakadi.client.scala.model.Event
import org.zalando.nakadi.client.scala.model.EventEnrichmentStrategy
import org.zalando.nakadi.client.scala.model.EventType
import org.zalando.nakadi.client.scala.model.EventTypeCategory
import org.zalando.nakadi.client.scala.model.EventValidationStrategy
import org.zalando.nakadi.client.scala.model._
import org.zalando.nakadi.client.scala.model.EventTypeStatistics
import org.zalando.nakadi.client.scala.model.EventTypeSchema
import org.zalando.nakadi.client.scala.model.SchemaType

trait EventGenerator {
  /**
   * Should generate an uniqueId that can be used for uniqueness of the Event,
   * this function MUST HAVE side effects.
   */
  def newId(): String

  /**
   * Should generate an new Event.
   * This function MUST HAVE side effects.
   */
  def newEvent(): Event

  /**
   * Returns the EventType.
   */
  def eventType: EventType = new EventType(name, //
    owner, //
    category, //
    validationStrategies, //
    enrichmentStrategies, //
    partitionStrategy, //
    schemaType, //
    dataKeyFields, //
    partitionKeyFields, //
    statistics)

  /**
   * Returns the EventTypeName.
   */

  def name: String
  /**
   * Returns the schema definition of the Event.
   */
  def schemaDefinition: String

  /**
   * Returns the owningApplication value. Default  = "Nakadi-klients(integration-test-suite)"
   */
  def owner: String = "Nakadi-klients(integration-test-suite)"

  /**
   * Returns the category value. Default = UNDEFINED
   */
  def category: EventTypeCategory.Value = EventTypeCategory.UNDEFINED

  /**
   * Returns the validationStrategies value. Default = Nil
   */
  def validationStrategies: Seq[EventValidationStrategy.Value] = Nil
  /**
   * Returns the enrichmentStrategies value. Default = Nil
   */
  def enrichmentStrategies: Seq[EventEnrichmentStrategy.Value] = Nil

  /**
   * Returns the partitionStrategy value. Default = Random
   */
  def partitionStrategy: Option[PartitionStrategy.Value] = Some(PartitionStrategy.RANDOM)

  /**
   * Returns the eventSchemaType value. Default = new EventTypeSchema(SchemaType.JSON, schemaDefinition)
   */
  def schemaType: EventTypeSchema = new EventTypeSchema(SchemaType.JSON, schemaDefinition)

  /**
   * Returns the dataKeyFields value. Default = Nil
   */
  def dataKeyFields: Seq[String] = Nil

  /**
   * Returns the partitionKeyFields value. Default = Nil
   */
  def partitionKeyFields: Seq[String] = Nil

  /**
   * Returns the partitionKeyFields value. Default = None
   */
  def statistics: Option[EventTypeStatistics] = None

}