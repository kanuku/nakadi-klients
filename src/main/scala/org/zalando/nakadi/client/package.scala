package org.zalando.nakadi


package object client {

  // Benjamin on PR21 comments (11-Feb-2016):
  //    "I would not make the URIs configurable as their are fixed by the REST API. However, I would change the default values"
  //
  val URI_METRICS = "/metrics"
  val URI_TOPICS = "/topics"
  val URI_EVENT_POST = "/topics/%s/events"
  val URI_PARTITIONS = "/topics/%s/partitions"
  val URI_PARTITION = "/topics/%s/partitions/%s"
  val URI_EVENTS_ON_PARTITION = "/topics/%s/partitions/%s/events"
  val URI_EVENT_LISTENING = "/topics/%s/partitions/%s/events?%s&batch_limit=%s&batch_flush_timeout=%s&stream_limit=%s"
}
