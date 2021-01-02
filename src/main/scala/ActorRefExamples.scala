package com.nila.akka

import akka.actor.{ ActorRef, ActorSystem, Props, Actor, Identify, ActorIdentity }

class Counter extends Actor {
  import Counter._

  var count = 0

  def receive = {
    case Inc(x) =>
      count += x
    case Dec(x) =>
      count -= x

  }

}

object Counter {

  final case class Inc(num: Int)
  final case class Dec(num: Int)
}

class Watcher extends Actor {

  var counterRef: ActorRef = _

  val selection = context.actorSelection("/user/counter")

  selection ! Identify(None)

  def receive = {
    case ActorIdentity(_, Some(ref)) =>
      println(s"Actor Reference for counter is ${ref}")
    case ActorIdentity(_, None) =>
      println("Actor selection for actor doesn't live :( ")

  }
}
object Watch extends App {

  val system = ActorSystem("Watsh-actor-selection")

  val counter = system.actorOf(Props[Counter], "counter")

  val watcher = system.actorOf(Props[Watcher], "watcher")

  Thread.sleep(1000)

  system.terminate()
}