package gr.fpas.tvs

import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

object VideoFramesReceiver {

  def framesWebsocket(implicit mat: ActorMaterializer,
                      ec: ExecutionContext): Flow[Message, Message, _] =
    Flow[Message]
      .mapAsync(1) {
      case TextMessage.Strict(msg) =>
        Future.successful(TextMessage("MSG" + msg))
      case st: TextMessage.Streamed =>
        st.toStrict(3.seconds).map(msg => TextMessage("STREAMED size:" + msg.text.length))
      case bm: BinaryMessage =>
        bm.dataStream.runWith(Sink.ignore)
        Future.successful(TextMessage("Unsupported binary message"))
    }
}
