package com.strad.evan.interpreters

import cats.~>
import com.rabbitmq.client._
import com.strad.evan.algebra.Bus.{Receive, MessageBusA, Send}
import fs2.Task
import io.circe.parser._

object RabbitMqInterpreter extends (MessageBusA ~> Task) {
  val queueName = "TestQueue"
  implicit val scheduler =
    _root_.fs2.Scheduler.fromFixedDaemonPool(2, "generator-scheduler")
  implicit val S =
    _root_.fs2.Strategy.fromFixedDaemonPool(2, "generator-timer")

  def apply[A](fa: MessageBusA[A]): Task[A] = {

    val factory = new ConnectionFactory()
    factory.setHost("localhost")
    val c = factory.newConnection()
    val ch: Channel = c.createChannel()
    val q = ch.queueDeclare(queueName, false, false, false, null)
    fa match {
      case Send(item) =>
        Task.delay(
          ch.basicPublish("", queueName, null, item.toString().getBytes)
        )
      case Receive() =>
        // FIXME: Returns a string
        Task.delay {
          val b = ch.basicGet(queueName, true)
          parse(new String(b.getBody)).getOrElse(throw new RuntimeException("Could not parse string from rabbit mq to Json"))
        }
    }
  }
}

