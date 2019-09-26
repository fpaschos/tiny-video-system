package gr.fpas.tvs

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink}
import gr.fpas.tvs.FeedReceiver.{Ack, Completed, Init}

import scala.concurrent.Future
import scala.concurrent.duration._

class VideoFramesReceiver(implicit val system: ActorSystem) {


  private val feedReceiver = system.actorOf(Props[FeedReceiver], "feed-receiver")

  def framesWebsocket: Flow[Message, Message, _] = {
    implicit val ec = system.dispatcher
    implicit val mat = ActorMaterializer()

    val feedSink = Sink.actorRefWithAck(feedReceiver, Init, Ack, Completed)

    Flow[Message]
      .mapAsync(1) {
        case TextMessage.Strict(msg) =>
          Future.successful(msg)
        case st: TextMessage.Streamed =>
          st.toStrict(3.seconds).map(_.text)
        case bm: BinaryMessage =>
          bm.dataStream.runWith(Sink.ignore)
          Future.successful("Unsupported binary message")
      }
      .alsoTo(feedSink)
      .map(msg => TextMessage(msg.length.toString))

  }
}

object VideoFramesReceiver {
  def apply(implicit system: ActorSystem) = new VideoFramesReceiver
}
