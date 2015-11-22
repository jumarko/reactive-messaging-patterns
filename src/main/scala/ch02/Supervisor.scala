package ch02

import java.util.concurrent.TimeUnit

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor._

import scala.concurrent.duration.FiniteDuration

/**
  * Demonstrates supervision strategies in akka.
  */
object Supervisor {
  def main(args: Array[String]) {
//    supervisedDemo()
    customSupervisionDemo()
  }


  def supervisedDemo(): Unit = {
    val supervised = ActorSystemDemo.system.actorOf(Props[DefaultSupervisorStrategy])

    supervised ! "ok"
    supervised ! "error"
    supervised ! "ok"
    // Kill message should be translated to ActorKilledException which means that actor will be stopped
    supervised ! Kill
    supervised ! "ok"
  }

  def customSupervisionDemo(): Unit = {
    val supervised = ActorSystemDemo.system.actorOf(Props[CustomSupervisorStrategy])

    supervised ! "ok"
    supervised ! "1/0"
    supervised ! "error"
    supervised ! "unsupported"
    supervised ! "ok"
    supervised ! "fatal"
  }

  class DefaultSupervisorStrategy extends Actor {

    @throws[Exception](classOf[Exception])
    override def preStart(): Unit = println("starting...")

    override def receive: Receive = {
      case "ok" => println("ok")
      case "error" => throw new RuntimeException("Error: actor should be restarted")
    }
  }

  class CustomSupervisorStrategy extends Actor {
    override def supervisorStrategy: SupervisorStrategy =
      OneForOneStrategy(maxNrOfRetries = 5, withinTimeRange = FiniteDuration(10, TimeUnit.SECONDS)) {
        // note that following syntax is wrong in original book text
        // (missing "_:" which results in "object ... is not a value" error)
        case _: NullPointerException => println("Npe => RESTART"); Restart
        case _: ArithmeticException => println("Arithmetic error => RESUME"); Resume
        case _: UnsupportedOperationException => println("Unsupported Op => STOP"); Stop
        case _: Exception => println("Fatal error => ESCALATE"); Escalate
      }

    @throws[Exception](classOf[Exception])
    override def preStart(): Unit = println("starting custom...")

    override def receive: Receive = {
      case "ok" => println("ok")
      case "error" => throw new NullPointerException("Npe => RESTART")
      case "1/0" => throw new ArithmeticException("Divison by zero - no effect on actor lifecycle => RESUME")
      case "unsupported" => throw new UnsupportedOperationException("Not supported => STOP")
      case "fatal" => throw new RuntimeException("Fatal error => ESCALATE")
    }
  }
}
