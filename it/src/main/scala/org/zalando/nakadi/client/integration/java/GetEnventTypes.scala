package org.zalando.nakadi.client.integration.java

import org.zalando.nakadi.client.scala.ClientFactory

object GetEnventTypes extends App {

  val client = ClientFactory.buildJavaClient()

  val t = client.getEventTypes.get
  println(">> " + t)

}
