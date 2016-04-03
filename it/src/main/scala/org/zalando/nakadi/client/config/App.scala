package org.zalando.nakadi.client

import org.zalando.nakadi.client.model.JacksonJsonMarshaller


object Main extends App with JacksonJsonMarshaller {
  val host = ""
  val OAuth2Token = () => ""
  val port = 443
  val client = new ClientImpl(Connection.newConnection(host, port, OAuth2Token, true, false),"UTF-8")

//  val response =  client.eventTypes()
//  Await.result(response, 10.second)
//  response.map(r =>
//    println("########################  " + r))

}