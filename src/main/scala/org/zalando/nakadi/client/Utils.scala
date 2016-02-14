package org.zalando.nakadi.client

import java.net.URI
import java.util
import java.util.Optional
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import akka.actor.ActorSystem
import akka.http.scaladsl.Http.OutgoingConnection
import akka.http.scaladsl.model.{HttpResponse, HttpRequest}
import akka.http.scaladsl.{Http, HttpsContext}
import akka.stream.scaladsl.Flow
import com.google.common.collect.Iterators

import scala.collection.JavaConversions
import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration
import collection.JavaConversions._

object Utils {

  private def extract[T](either: Either[String, T]): T = either match {
    case Left(error) => throw new RuntimeException(error)
    case Right(t) => t
  }


  def convert[T](x: scala.concurrent.Future[Either[String, T]]): java.util.concurrent.Future[T] =
    new MFuture[Either[String, T], T](x, a => extract(a))


  def convertSimple[T](x: scala.concurrent.Future[T]): java.util.concurrent.Future[T] = new MFuture[T, T](x, a => a)


  def convertForMapRetrieval[A](x: scala.concurrent.Future[Either[String, Map[A, Any]]]): java.util.concurrent.Future[java.util.Map[A, Any]] = {

    def map(m: java.util.Map[A, Any]): java.util.Map[A, Any] = {
      val newMap = new util.HashMap[A, Any]()
      val iter = m.entrySet().iterator();
      while (iter.hasNext) {
        val e = iter.next()
        val v = e.getValue
        if(! v.isInstanceOf[String])
          newMap.put(e.getKey, map(mapAsJavaMap(v.asInstanceOf[Map[A, Any]])).asInstanceOf[Any])
        else
          newMap.put(e.getKey, v)
      }
      newMap
    }

    new MFuture[Either[String, Map[A, Any]], java.util.Map[A, Any]](x, a => map(mapAsJavaMap(extract(a))))
  }



  def convertForListRetrieval[A](x: scala.concurrent.Future[Either[String, List[A]]]): java.util.concurrent.Future[java.util.List[A]] =
    new MFuture[Either[String, List[A]], java.util.List[A]](x, a => {
      val l: List[A] = extract(a)
      new util.ArrayList[A](l)
  })


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


private class MFuture[A, B](f: scala.concurrent.Future[A], converter: A => B) extends java.util.concurrent.Future[B]{
  override def isCancelled: Boolean = throw new UnsupportedOperationException

  override def get(): B = converter.apply(Await.result(f, Duration.Inf))

  override def get(timeout: Long, unit: TimeUnit): B = converter.apply(Await.result(f, Duration.create(timeout, unit)))

  override def cancel(mayInterruptIfRunning: Boolean): Boolean = throw new UnsupportedOperationException

  override def isDone: Boolean = f.isCompleted
}
