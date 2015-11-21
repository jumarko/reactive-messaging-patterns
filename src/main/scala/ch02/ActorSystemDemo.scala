package ch02

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ActorSystemDemo {
  val system = ActorSystem("ReactiveEnterprise")

  def demo(): Unit = {
    val processManagersRef: ActorRef = system.actorOf(Props[ProcessManagers], "processManagers")
    processManagersRef ! BrokerForLoan(List("KB", "CSOB"))
    println("message sent")

    val allUserActors = system.actorSelection("/user/*")
    allUserActors ! FlushAll()

    Thread.sleep(1000)
    system.terminate()
  }


  def main(args: Array[String]) {
    ActorSystemDemo.demo()
  }
}

class ProcessManagers extends Actor {
  // the simplest possible "do-nothing" implementation of Actor
  override def receive: Receive = {
    case BrokerForLoan(banks) => println("Recieved banks: " + banks)
    case _ => println("Unknown message")
  }

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = println("preStart")

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = println("postStop")
}

case class BrokerForLoan(banks: List[String])

case class FlushAll()
