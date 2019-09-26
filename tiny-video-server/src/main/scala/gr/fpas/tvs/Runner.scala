package gr.fpas.tvs

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import scala.io.StdIn

object Runner extends App {
  implicit val system = ActorSystem("tiny-video-server")
  implicit val mat = ActorMaterializer()

  import akka.http.scaladsl.server.Directives._
  import system.dispatcher

  // TODO dependecy injection
  val videoFramesReceiver = VideoFramesReceiver.apply

  val route = get {
    pathEndOrSingleSlash {
      complete("Websocket server")
    } ~
      path("ws") {
        handleWebSocketMessages(videoFramesReceiver.framesWebsocket)
      }
  }


  val binding = Http().bindAndHandle(route, "0.0.0.0", 8080)

  println(s"Server is now online at http://0.0.0.0:8080\nPress RETURN to stop...")
  StdIn.readLine()

  println("Server is terminating...")
  binding.flatMap(_.unbind()).onComplete(_ => system.terminate())
}
