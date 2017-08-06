package com.strad.evan.interpreters

import cats.~>
import com.rabbitmq.client.ConnectionFactory
import com.strad.evan.algebra.Bus.{MessageBusA, Send}
import fs2.Task

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
    val ch = c.createChannel()
    val q = ch.queueDeclare(queueName, false, false, false, null)

    fa match {
      case Send(item) =>
        Task.delay(
          ch.basicPublish("", queueName, null, item.toString().getBytes)
        )
      case _ =>
        Task.fail(new NotImplementedError())
    }
  }
}

