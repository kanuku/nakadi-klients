package org.zalando.nakadi.client.subscription

import org.zalando.nakadi.client.scala.model.Event

case class MyEventExample(orderNumber: String) extends Event