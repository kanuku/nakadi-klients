package org.zalando.nakadi.client

import java.io.{IOException, ByteArrayOutputStream}
import java.net.URI

import akka.actor.ActorSystem
import akka.http.javadsl.model
import akka.http.scaladsl
import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler
import com.fasterxml.jackson.databind._
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.zalando.nakadi.client.actor.{ConnectionOpened, ConnectionClosed}

import scala.concurrent.Await
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._

import scala.concurrent.Future



import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration


object Main {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

// http://doc.akka.io/docs/akka-stream-and-http-experimental/2.0.2/scala/stream-quickstart.html

  def main (args: Array[String]) {

    val klient = KlientBuilder()
      .withEndpoint(new URI("eventstore-laas.laas.zalan.do")) // eventstore-laas.laas.zalan.do
      .withPort(8080)
      .withTokenProvider(() => "6165764c-05cc-4d32-9ede-64bede084b51").build()

    val listener = new Listener {

      override def onReceive(topic: String, partition: String, cursor: Cursor, event: Event): Unit = println(s">>>>> [event=$event, partition=$partition]")

      override def onConnectionClosed(topic: String, partition: String, lastCursor: Option[Cursor]): Unit = println(s"connection closed [partition=$partition]")

      override def onConnectionOpened(topic: String, partition: String): Unit = println(s"connection opened [partition=$partition]")

      override def onConnectionFailed(topic: String, partition: String, status: Int, error: String): Unit = println(s"connection failed [topic=$topic, partition=$partition, status=$status, error=$error]")
    }

    klient.subscribeToTopic("items", ListenParameters(Some("0")), listener, true)

    Thread.sleep(Long.MaxValue)


  }
}