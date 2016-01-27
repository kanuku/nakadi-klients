package org.zalando.nakadi.client.actor

import java.net.URI
import java.util.concurrent.TimeUnit

import akka.actor._
import akka.util.Timeout
import com.fasterxml.jackson.databind.ObjectMapper
import org.zalando.nakadi.client.{Listener, KlientException, ListenParameters}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}


case class NewSubscription(topic: String,
                           partitionId: String,
                           parameters: ListenParameters,
                           autoReconnect: Boolean,
                           listener: Listener)

case class Unsubscription(topic: String,
                          partitionId: String,
                          listener: Listener)

object KlientSupervisor{
  def props(endpoint: URI, port: Int, securedConnection: Boolean, tokenProvider: () => String, objectMapper: ObjectMapper) =
                                           Props(new KlientSupervisor(endpoint, port, securedConnection, tokenProvider, objectMapper))
}

class KlientSupervisor(val endpoint: URI, val port: Int, val securedConnection: Boolean, val tokenProvider: () => String, val objectMapper: ObjectMapper)
  extends Actor with ActorLogging{

  import akka.actor.SupervisorStrategy._
  import scala.concurrent.duration._
  import scala.language.postfixOps

  override val supervisorStrategy =
    AllForOneStrategy(maxNrOfRetries = 100, withinTimeRange = 5 minutes) {
      case e: ArithmeticException      => println("xxx ARITHMETIC"); Resume
      case e: NullPointerException     => println("xxx NULLPOINTER"); Restart
      case e: IllegalArgumentException => println("xxx ILLEGAL"); Stop
      case e: Exception                => println("xxx ANY EXC"); e.printStackTrace(); Resume//Escalate
    }


  var listenerActorPathToReceiverAndListenerMap = Map[String, (ActorRef, ActorRef)]()


  override def receive: Receive = {
    case NewSubscription(topic, partitionId, parameters, autoReconnect, listener) => {
      resolveActor(s"/user/partition-$partitionId").onComplete(_ match {
        case Success(receiverActor) => registerListener(topic, partitionId, listener, receiverActor)
        case Failure(e: ActorNotFound) =>
          val receiverActor = context.actorOf(
            PartitionReceiver.props(endpoint,
              port,
              securedConnection,
              topic,
              partitionId,
              parameters,
              tokenProvider,
              autoReconnect,
              objectMapper),
            s"partition-$partitionId")
          registerListener(topic, partitionId, listener, receiverActor)
        case Failure(e: Throwable) => throw new KlientException(e.getMessage, e)
      })
    }
    case Unsubscription(topic, partitionId, listener) => {
      unregisterListener(topic, partitionId, listener)
    }
  }


  def asListenerPath(topic: String, partitionId: String, listener: Listener): String = s"$topic-$partitionId-${listener.id}"


  def resolveActor(actorSelectionPath: String): Future[ActorRef] = {
    val receiverSelection = context.actorSelection(actorSelectionPath)
    receiverSelection.resolveOne()(Timeout(1L, TimeUnit.SECONDS))
  }


  def registerListener(topic: String, partitionId: String, listener: Listener, receiverActor: ActorRef) = {
    val listenerPath: String = asListenerPath(topic, partitionId, listener)
    val listenerActor = context.actorOf(ListenerActor.props(listener), listenerPath)
    receiverActor ! NewListener(listenerActor)
    listenerActorPathToReceiverAndListenerMap =
                                listenerActorPathToReceiverAndListenerMap ++ Map((listenerPath, (receiverActor, listenerActor)))
  }


  def unregisterListener(topic: String, partitionId: String, listener: Listener) = {
    val listenerPath = asListenerPath(topic, partitionId, listener)
    listenerActorPathToReceiverAndListenerMap.get(listenerPath) match {
      case Some((listenerActor, receiverActor)) => {
        receiverActor ! Unregistered(listenerActor)
        listenerActor ! PoisonPill.getInstance
        listenerActorPathToReceiverAndListenerMap = listenerActorPathToReceiverAndListenerMap - listenerPath
        log.debug("unregistered listener for [topic={}, partitionId={}, listener.id={}]", topic, partitionId, listener.id)
      }
      case None => log.warning("no listener registered for [topic={}, partitionId={}, listener.id={}]",
                               topic, partitionId, listener.id )
    }

  }
}
