package org.zalando.nakadi.client.utils

object Uri {
  
  final val URI_METRICS = "/metrics"
  
  /*Events*/
  final val URI_EVENT_TYPES = "/event-types"
  final val URI_EVENT_TYPE_BY_NAME = "/event-types/%s"
  final val URI_EVENTS_OF_EVENT_TYPE = "/event-types/%s/events"

  /*Partitions*/
  final val URI_PARTITIONS_BY_EVENT_TYPE = "/event-types/%s/partitions"

  /*Strategies*/
  final val URI_VALIDATION_STRATEGIES = "/registry/validation-strategies"
  final val URI_ENRICHMENT_STRATEGIES = "/registry/enrichment-strategies"
  final val URI_PARTITIONING_STRATEGIES = "/registry/partition-strategies"
}