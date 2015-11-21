package ch02

import akka.actor.{Actor, Props}

import scala.util.Random

class Task extends Actor {
  override def receive: Actor.Receive = {
    case Run(definition) => println("running definition: " + definition)
  }
}

case class RunTask(definition: String)

case class Run(definition: String)

case class TaskCompleted()

class TaskManager extends Actor {
  def nextTaskName(): String = {
    "task-" + Random.nextInt()
  }
  override def receive: Receive = {
    case RunTask(definition) =>
      val task = context.actorOf(Props[Task], nextTaskName())
      task ! Run("definition")

    case TaskCompleted =>
  }

}

object TaskManager {
  def main(args: Array[String]) {
    val taskManager = ActorSystemDemo.system.actorOf(Props[TaskManager])
    taskManager ! RunTask("run task definition")
  }
}
