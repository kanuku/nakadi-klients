package org.zalando.nakadi.client.scoop


import java.util.UUID

import akka.cluster.{Cluster, Member}
import com.typesafe.scalalogging.Logger
import de.zalando.scoop.{ScoopListener, ScoopClient}
import org.slf4j.LoggerFactory
import org.zalando.nakadi.client._
import scala.concurrent.ExecutionContext.Implicits.global

object ScoopClientWrapper {
  val UNREACHABLE_MEMBER_EVENT_TYPE: String = "/scoop-system/unreachable-member"
  val UNREACHABLE_MEMBER_EVENT_BODY_KEY: String = "unreachable_member"

  def apply(klient: Klient, scoopClient: ScoopClient, scoopTopic: String) =
                                                                new ScoopClientWrapper(klient, scoopClient, scoopTopic)
}

class ScoopClientWrapper private (val klient: Klient, val scoopClient: ScoopClient, val scoopTopic: String)
  extends ScoopClient with ScoopListener with Listener {

  import ScoopClientWrapper._

  val logger = Logger(LoggerFactory.getLogger("ScoopClientWrapper"))
  var cluster: Option[Cluster] = None

  var mayProcess: Boolean = true

  val ID = s"scoop-client-wrapper-${System.nanoTime()}"

  klient.subscribeToTopic(scoopTopic, ListenParameters(), this)

  override def init(newCluster: Cluster): Unit = cluster = Some(newCluster)

  override def id: String = ID

  override def isHandledByMe(id: String): Boolean = mayProcess && scoopClient.isHandledByMe(id)

  override def onReceive(topic: String, partition: String, cursor: Cursor, event: Event) = event match {
    case Event(UNREACHABLE_MEMBER_EVENT_TYPE, _,  _, body) =>
      logger.info("received event of [eventType={}] -> [event={}]", UNREACHABLE_MEMBER_EVENT_TYPE, event)
      body.asInstanceOf[Map[String, AnyRef]].get(UNREACHABLE_MEMBER_EVENT_BODY_KEY) match {
        case Some(unreachableMember: String) =>
          cluster match{
            case Some(actualCluster) =>
              val meAsMemberString = actualCluster.selfAddress.toString
              if(unreachableMember == meAsMemberString) {
                logger .info("I [address={}] am NOT reachable by the rest of my cluster :-( " +
                             "-> disconnecting" + " (you might consider restarting this instance)",
                             meAsMemberString)
                mayProcess = false
              }
            case None => logger.warn("I have not been initialized with a cluster instance yet " +
                                     "-> ignoring [event={}]", event)
          }
        case Some(unknown) => logger.warn("could not [entry={}] from body of [event={}] as String entry is expected " +
                                           "-> ignored", UNREACHABLE_MEMBER_EVENT_BODY_KEY, event)
        case None =>
          logger.warn("[event={}] does not contain [bodyKey={}] -> ignored", event, UNREACHABLE_MEMBER_EVENT_BODY_KEY)
      }
  }

  override def onMemberUp(member: Member): Unit = if ((! mayProcess) && isItMe(member)) {
    mayProcess = true
    logger.info("I am allowed to process events as my cluster can see me again :-)")
  }

  /**
   * If this instance is the cluster leader, it has to notify all other members about unreachable member
   * because this allows the unreachable member to get known about the fact it is not reachable by the cluster.
   * As a consequence, the unreachable member can stop processing events as it is not considered by the cluster
   * anymore.
   */
  override def onMemberUnreachable(member: Member): Unit = {

    cluster match {
      case Some(actualCluster) =>
        if(actualCluster.selfRoles.contains("leader")) { // TODO right role name?
          logger.info("I AM the LEADER and I am notifying other Scoop aware clients about [unreachableMember={}]", member)
          val event = Event(UNREACHABLE_MEMBER_EVENT_TYPE,
                            "scoop-system",
                            Map("id" -> UUID.randomUUID().toString),
                            Map(UNREACHABLE_MEMBER_EVENT_BODY_KEY -> member.address.toString))

          klient.postEvent(scoopTopic, event).map(error =>
                                    logger.warn("could not notify cluster about unreachable member [error={}]", error))
      }
      else logger.debug("received event about [unreachableMember={}] but I am NOT the LEADER -> ignored", member)

      case None => logger.warn("I have not been initialized with a cluster instance yet " +
                               "-> ignoring MemberUnreachable([member={}])", member)
    }
  }

  override def onMemberRemoved(member: Member): Unit = if (mayProcess && isItMe(member)) {
    mayProcess = false
    logger.info("I have been removed from the cluster and therefore I refuse to process any events from now on" +
                " -> you should consider restarting the instance")
  }

  private def isItMe(member: Member): Boolean = cluster match {
      case Some(actualCluster) => actualCluster.selfAddress == member.address
      case None => false
  }

  override def onRebalanced(i: Int, i1: Int): Unit = ()
  override def onConnectionOpened(topic: String, partition: String) = ()
  override def onConnectionFailed(topic: String, partition: String, status: Int, error: String) = ()
  override def onConnectionClosed(topic: String, partition: String, lastCursor: Option[Cursor]) = ()

}
