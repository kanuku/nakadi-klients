package org.zalando.nakadi.client.integration.java

import org.zalando.nakadi.client.utils.ClientBuilder

object GetEnventTypes extends App {

  val client = new ClientBuilder().buildJavaClient()

  val t = client.getEventTypes.get
  println(">> " + t)

}
