package gr.fpas.tvs

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.io.StdIn

object Runner extends App {
  implicit val system = ActorSystem("tiny-video-server")
  implicit val mat = ActorMaterializer()

  import system.dispatcher

  import akka.http.scaladsl.server.Directives._

  val route = get {
    pathEndOrSingleSlash {
      complete("Websocket server")
    } ~
      path("ws") {
        handleWebSocketMessages(VideoFramesReceiver.framesWebsocket)
      }
  }




  val binding = Http().bindAndHandle(route, "0.0.0.0", 8080)

  println(s"Server is now online at http://0.0.0.0:8080\nPress RETURN to stop...")
  StdIn.readLine()

  println("Server is terminating...")
  binding.flatMap(_.unbind()).onComplete(_ => system.terminate())
}
