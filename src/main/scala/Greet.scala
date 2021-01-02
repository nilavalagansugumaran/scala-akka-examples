
import akka.actor.{Actor, ActorSystem, Props}

case class WhoToGreet(who: String)

class GreatActor extends Actor {
  override def receive: Receive = {
    case msg: WhoToGreet => {
      println(s"Hello ${msg.who}")
    }
  }
}

object GreaterApp extends App {

  val system = ActorSystem("Greeting")

      val greatActor = system.actorOf(Props[GreatActor], "greeting-actor")
      var continueGreet: Boolean = false
      do {
        println("Name please..")
        val name = scala.io.StdIn.readLine()
        greatActor ! WhoToGreet(name)
        println("Do you want to continue? (true/false)")
        continueGreet = scala.io.StdIn.readBoolean()
      } while(continueGreet)

  system.terminate()
}