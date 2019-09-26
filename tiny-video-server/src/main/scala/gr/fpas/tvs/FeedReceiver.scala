package gr.fpas.tvs

import akka.actor.Actor
import akka.event.Logging

class FeedReceiver extends Actor {
  import FeedReceiver._

  private val log = Logging.getLogger(context.system, this)

  override def receive: Receive = {
    case _: Init.type =>
      sender ! Ack
    case Completed =>
      log.debug(s"Websocket terminated")
      sender ! Ack
    case _ => sender ! Ack
  }
}

object FeedReceiver {
  case object Init
  case object Ack
  case object Completed
}
