package org.zalando.nakadi.client.utils

import java.util.concurrent.TimeUnit
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import collection.JavaConversions._


object Conversions {
  
   
  private def extract[T](either: Either[String, T]): T = either match {
    case Left(error) => throw new RuntimeException(error)
    case Right(t) => t
  }
  
//  def metricsDeserializer=  
  
  
  def convert[T](x: scala.concurrent.Future[Either[String, T]]): java.util.concurrent.Future[T] =
    new MFuture[Either[String, T], T](x, a => extract(a))
  
  
}
  
  private class MFuture[A, B](f: scala.concurrent.Future[A], converter: A => B) extends java.util.concurrent.Future[B]{
  override def isCancelled: Boolean = throw new UnsupportedOperationException

  override def get(): B = converter.apply(Await.result(f, Duration.Inf))

  override def get(timeout: Long, unit: TimeUnit): B = converter.apply(Await.result(f, Duration.create(timeout, unit)))

  override def cancel(mayInterruptIfRunning: Boolean): Boolean = throw new UnsupportedOperationException

  override def isDone: Boolean = f.isCompleted
}
