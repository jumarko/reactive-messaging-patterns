package ch02

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, Props}

import scala.concurrent.duration.FiniteDuration
import scala.util.Random

// for scheduling
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * This implementation switches between houseKeeper method and taskDistributor method for receive very easily.
  *
  * Note that we still need to implement receive method although it's never called.
  */
class DynamicTaskManager extends Actor {
  def nextTaskName(): String = {
    "task-" + Random.nextInt()
  }


  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    context.become(houseKeeper)
  }

  def houseKeeper: Receive = {
    case StartTaskManagement(externalStatusWatcher) =>
      context.become(taskDistributor)
      // following is just a demonstration of various context methods
      println("/user/* actorSelection: " + context.actorSelection("/user/*"))
      println("children: " + context.children)
      println("parent: " + context.parent)
      println("props: " + context.props)
      println("self: " + context.self)
      println("sender(): " + context.sender())
      // watchout for using sender inside closure because it's evaluated only when it's called!
      // e.g. following usage is not valid - sender will be very probably other actor than the one we want to refer
      // -> in this case it's "deadLetters" actor
      context.system.scheduler.scheduleOnce(FiniteDuration(5, TimeUnit.SECONDS)) {
        sender() ! "Unifinished calculation"
      }
      // we need to do it in following way
      val requestSender = sender()
      context.system.scheduler.scheduleOnce(FiniteDuration(5, TimeUnit.SECONDS)) {
        requestSender ! "Unifinished calculation"
      }
  }

  def taskDistributor: Receive = {
    case RunTask(definition) =>
      val task = context.actorOf(Props[Task], nextTaskName())
      task ! Run(definition)

    case TaskCompleted =>
  }

  // this should never be called
  override def receive: Actor.Receive = {
    case _ => println("receive called")
  }
}

case class StartTaskManagement(externalStatusWatcher: String)

class SenderActor extends Actor {
  override def receive: Actor.Receive = {
    case _ => println("I'm the sender")
  }
}


object DynamicTaskManager {
  def main(args: Array[String]) {
    val taskManager = ActorSystemDemo.system.actorOf(Props[DynamicTaskManager])

    implicit val sender = ActorSystemDemo.system.actorOf(Props[SenderActor])

    taskManager ! StartTaskManagement("status watcher")
    taskManager ! RunTask("after start we can run task")
  }
}
