package org.zalando.nakadi.client.event

import akka.stream.actor.WatermarkRequestStrategy
import java.math.BigInteger
import akka.actor.ActorLogging
import akka.stream.actor.ActorSubscriber
import akka.stream.actor.ActorSubscriberMessage.OnError
import akka.stream.actor.ActorSubscriberMessage.OnNext
import akka.stream.actor.ActorSubscriberMessage.OnComplete

class SlowSubscriber(delay: Long) extends ActorSubscriber with ActorLogging { // 1
  val requestStrategy = WatermarkRequestStrategy(50) // 2

  def receive = {
    case OnNext(fib: BigInteger) => // 3
      log.debug("[SlowSubscriber] Received Fibonacci Number: {}", fib)
      Thread.sleep(delay)
    case OnError(err: Exception) => // 4
      log.error(err, "[SlowSubscriber] Receieved Exception in Fibonacci Stream")
      context.stop(self)
    case OnComplete => // 5
      log.info("[SlowSubscriber] Fibonacci Stream Completed!")
      context.stop(self)
    case _ =>
       log.info("[SlowSubscriber] Unknown!")
  }
  
  
}
