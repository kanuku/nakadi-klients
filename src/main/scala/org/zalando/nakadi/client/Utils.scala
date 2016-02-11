package org.zalando.nakadi.client

import java.net.URI
import java.util.Optional
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import akka.actor.ActorSystem
import akka.http.scaladsl.Http.OutgoingConnection
import akka.http.scaladsl.model.{HttpResponse, HttpRequest}
import akka.http.scaladsl.{Http, HttpsContext}
import akka.stream.scaladsl.Flow

import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration
import collection.JavaConversions._

object Utils {
  def convert[T](x: scala.concurrent.Future[T]): java.util.concurrent.Future[T]={
    new java.util.concurrent.Future[T] {
      override def isCancelled: Boolean = throw new UnsupportedOperationException

      override def get(): T = Await.result(x, Duration.Inf)

      override def get(timeout: Long, unit: TimeUnit): T = Await.result(x, Duration.create(timeout, unit))

      override def cancel(mayInterruptIfRunning: Boolean): Boolean = throw new UnsupportedOperationException

      override def isDone: Boolean = x.isCompleted
    }
  }


  def convertForMapRetrieval[A, B](x: scala.concurrent.Future[Either[String, Map[A, B]]]): java.util.concurrent.Future[java.util.Map[A, B]]={
    new java.util.concurrent.Future[java.util.Map[A, B]] {
      override def isCancelled: Boolean = throw new UnsupportedOperationException

      override def get(): java.util.Map[A, B] =
         Await.result(x, Duration.Inf) match {
           case Left(error) => throw new RuntimeException(error)
           case Right(map) => map
         }

      override def get(timeout: Long, unit: TimeUnit): java.util.Map[A, B] =
        Await.result(x, Duration.create(timeout, unit)) match {
          case Left(error) => throw new RuntimeException(error)
          case Right(map) => map
        }

      override def cancel(mayInterruptIfRunning: Boolean): Boolean = throw new UnsupportedOperationException

      override def isDone: Boolean = x.isCompleted
    }
  }

  def convertForListRetrieval[A](x: scala.concurrent.Future[Either[String, List[A]]]): java.util.concurrent.Future[java.util.List[A]]={
    new java.util.concurrent.Future[java.util.List[A]] {
      override def isCancelled: Boolean = throw new UnsupportedOperationException

      override def get(): java.util.List[A] =
        Await.result(x, Duration.Inf) match {
          case Left(error) => throw new RuntimeException(error)
          case Right(list) => list
        }

      override def get(timeout: Long, unit: TimeUnit): java.util.List[A] =
        Await.result(x, Duration.create(timeout, unit)) match {
          case Left(error) => throw new RuntimeException(error)
          case Right(list) => list
        }

      override def cancel(mayInterruptIfRunning: Boolean): Boolean = throw new UnsupportedOperationException

      override def isDone: Boolean = x.isCompleted
    }
  }

  def convertToOptional[A](option: Option[A]): Optional[A] =
    option match {
      case Some(v) => Optional.of(v)
      case None => Optional.empty()
    }


  def outgoingHttpConnection(endpoint: URI, port: Int, securedConnection: Boolean)(implicit system: ActorSystem): Flow[HttpRequest, HttpResponse, Future[OutgoingConnection]] = {
    val http = Http(system)
    if(securedConnection) {
      http.setDefaultClientHttpsContext(HttpsContext(SSLContext.getDefault))
      http.outgoingConnectionTls(endpoint.toString, port)
    }

    else http.outgoingConnection(endpoint.toString, port)
  }
}
