package org.zalando.nakadi.client.utils

import java.util.Optional
import java.util.concurrent.TimeUnit

import scala.collection.JavaConversions._
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object FutureConversions {
  //
  private def extractEither[T](either: Either[String, T]): T = either match {
    case Left(error) => throw new RuntimeException(error)
    case Right(t) => t
  }

  def fromOption2Optional[T](in: scala.concurrent.Future[Option[T]])
    : java.util.concurrent.Future[Optional[T]] = {
    new MFuture[Option[T], Optional[T]](in, a => fromOptional2Optional(a))
  }
  def fromOption2Void[T](in: scala.concurrent.Future[Option[T]])
    : java.util.concurrent.Future[Void] = {
    new MFuture[Option[T], Void](in, a => null)
  }
  def fromFuture2Future[T](
      in: scala.concurrent.Future[T]): java.util.concurrent.Future[T] = {
    new MFuture[T, T](in, a => a)
  }
  def fromFuture2FutureVoid[T](
      in: scala.concurrent.Future[T]): java.util.concurrent.Future[Void] = {
    new MFuture[T, Void](in, a => null)
  }

  private def fromSequenceToList[T](in: Seq[T]): Optional[java.util.List[T]] =
    in match {
      case Nil => Optional.empty()
      case seq => Optional.of(new java.util.ArrayList[T](seq))

    }

  private def fromOptional2Optional[R](in: Option[R]): Optional[R] = in match {
    case Some(value) => Optional.of(value)
    case None => Optional.empty()
  }

  private def convert[T](x: scala.concurrent.Future[Either[String, T]])
    : java.util.concurrent.Future[T] =
    new MFuture[Either[String, T], T](x, a => extractEither(a))

}

private class MFuture[A, B](f: scala.concurrent.Future[A], converter: A => B)
    extends java.util.concurrent.Future[B] {
  override def isCancelled: Boolean = throw new UnsupportedOperationException

  override def get(): B = converter.apply(Await.result(f, Duration.Inf))

  override def get(timeout: Long, unit: TimeUnit): B =
    converter.apply(Await.result(f, Duration.create(timeout, unit)))

  override def cancel(mayInterruptIfRunning: Boolean): Boolean =
    throw new UnsupportedOperationException

  override def isDone: Boolean = f.isCompleted
}
