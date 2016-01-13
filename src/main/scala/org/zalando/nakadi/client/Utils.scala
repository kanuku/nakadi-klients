package org.zalando.nakadi.client

import java.util.concurrent.{TimeUnit, Future}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * Created by bfriedrich on 11/01/16.
 */
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
}
