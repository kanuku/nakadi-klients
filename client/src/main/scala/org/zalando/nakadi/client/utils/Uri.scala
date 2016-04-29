package org.zalando.nakadi.client.utils

object Uri {
  
  def URI_METRICS = "/metrics"
  
  /*Events*/
  def URI_EVENT_TYPES = "/event-types"
  def URI_EVENT_TYPE_BY_NAME = "/event-types/%s"
  def URI_EVENTS_OF_EVENT_TYPE() = "/event-types/%s/events"
  def getEventStreamingUri(in:String) = s"/event-types/$in/events"
  

  /*Partitions*/
  final def URI_PARTITIONS_BY_EVENT_TYPE = "/event-types/%s/partitions"

  /*Strategies*/
  final def URI_VALIDATION_STRATEGIES = "/registry/validation-strategies"
  final def URI_ENRICHMENT_STRATEGIES = "/registry/enrichment-strategies"
  final def URI_PARTITIONING_STRATEGIES = "/registry/partition-strategies"
}