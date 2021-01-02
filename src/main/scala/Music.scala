import MusicController.{Play, Stop}
import MusicPlayer.{StartMusic, StopMusic}
import akka.actor.{Actor, ActorSystem, Props}


object MusicController {
  sealed trait ControllerMsg
  case object Stop extends ControllerMsg
  case object Play extends ControllerMsg

  def props = Props[MusicController]
}

class MusicController extends Actor {
  override def receive: Receive = {
    case Stop => {
      println("Stopping music...")
    }
    case Play => {
      println("Playing music...")
    }
  }
}
object MusicPlayer {
  sealed trait PlayMsg
  case object StartMusic extends PlayMsg
  case object StopMusic extends PlayMsg
}
class MusicPlayer extends Actor {
  override def receive: Receive = {
    case StopMusic => {
      println("I dont want to stop music")
    }
    case StartMusic => {
      val controller = context.actorOf(MusicController.props, "controller")
      controller ! Play
    }
    case _ =>
      println("Unknown message")
  }
}

object MusicApp extends App {

  val system = ActorSystem("music")

  val musicPlayer = system.actorOf(Props[MusicPlayer], "musicplayer")
  musicPlayer ! StartMusic
  system.terminate()
}