package org.zalando.nakadi.client.actor.utils

import akka.actor.SupervisorStrategy._
import akka.actor.SupervisorStrategy
import akka.actor.ActorInitializationException
import akka.actor.ActorKilledException
import akka.actor.OneForOneStrategy

trait SupervisorHelper {

  def escalate: SupervisorStrategy = {
    def defaultDecider: Decider = {
      case _: ActorInitializationException ⇒ Stop
      case _: ActorKilledException         ⇒ Stop
      case _: Throwable                    ⇒ Stop
    }
    OneForOneStrategy()(defaultDecider)
  }

}