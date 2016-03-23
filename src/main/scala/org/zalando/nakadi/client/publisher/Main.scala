package org.zalando.nakadi.client.publisher

import java.math.BigInteger
import akka.actor.ActorSystem
import akka.stream.actor.ActorSubscriber
import akka.stream.actor.ActorPublisher
import akka.actor.Props

//object MainApp extends App {
//  implicit val system = ActorSystem("test")
//
//  println("Creating EventPublisher!")
//  val pubRef = system.actorOf(Props[EventPublisher])
//  val publisher = ActorPublisher[BigInteger](pubRef) // => org.reactivestreams.Publisher
//
//  println("Creating SlowSubscriber!")
//  val subRef = system.actorOf(Props(new SlowSubscriber(500L))) // 500 ms delay
//  val subscriber = ActorSubscriber[BigInteger](subRef) // => org.reactivestreams.Subscriber
//  publisher.subscribe(subscriber)
//}