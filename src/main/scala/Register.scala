import Checker.{CheckUser, InValidUser, ValidUser}
import Recorder.NewUser
import Storage.StoreUser
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._

case class User(name: String, email: String)

object Recorder {
  sealed trait RecorderMsg
  case class NewUser(user: User) extends RecorderMsg
}
object Checker {
  sealed trait CheckerMsg
  case class CheckUser(user: User) extends CheckerMsg

  sealed trait CheckerResponse
  case class ValidUser(user: User) extends CheckerResponse
  case class InValidUser(user: User) extends CheckerResponse
}
object Storage {
  sealed trait StorageMsg
  case class StoreUser(user: User) extends StorageMsg
}

class Storage extends Actor {

  var users = List.empty[User]

  override def receive: Receive = {
    case StoreUser(user: User) => {
      println(s"user added $user")
      users = user :: users
    }
  }
}

class Checker extends Actor {

  override def receive: Receive = {
    case CheckUser(user: User) => {
      println(s"checking user... $user")
      if(user.name.equals("Unknown")) {
        println(s"user $user invalid")
        sender() ! InValidUser(user)
      } else
        println(s"user $user valid")
        sender() ! ValidUser(user)
    }
  }
}

class Recorder(checker: ActorRef, storage: ActorRef) extends Actor {
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.language.postfixOps

  implicit val timeout = Timeout(5 seconds)

  override def receive: Receive = {
    case NewUser(user: User) => {
      println(s"user received... $user")

        checker ? CheckUser(user) map {
          case InValidUser(inValidUser) => {
            println(s"user invalid $inValidUser")
          }
          case ValidUser(validUser) => {
            storage ! StoreUser(validUser)
          }
      }
    }
  }
}
object RegisterApp extends App {

  val system = ActorSystem("register")
  var checker = system.actorOf(Props[Checker], "checker")
  var storage = system.actorOf(Props[Storage], "storage")

  var recorder = system.actorOf(Props(new Recorder(checker, storage)), "recorder")
  recorder ! Recorder.NewUser(User("nila", "nila@nila.com"))
  Thread.sleep(1000)
  recorder ! Recorder.NewUser(User("Unknown", "Unknown@Unknown.com"))
  system.terminate()
}