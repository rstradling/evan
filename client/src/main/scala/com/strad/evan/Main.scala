package com.strad.evan

import cats.free.Free
import com.strad.evan.App.CommandApp
import com.strad.evan.algebra.Bus.MessageBus
import com.strad.evan.interpreters.CommandInterpreter
import io.circe._
import io.circe.parser._
import fs2.interop.cats._

object Main extends App {

  import algebra.EventStore._
  import scala.concurrent._
   implicit val scheduler =
    _root_.fs2.Scheduler.fromFixedDaemonPool(2, "generator-scheduler")
  implicit val S =
    _root_.fs2.Strategy.fromFixedDaemonPool(2, "generator-timer")
 import scala.concurrent.duration._

  def program(implicit
              I: EventStore[CommandApp],
              B: MessageBus[CommandApp]): Free[CommandApp, Unit] = {
    import I._, B._
    val answer: Json = parse("""{
                               |  "data": "yours",
                               |  "mine": "theirs",
                               |  "test": "mine"
                               | }
                             """.stripMargin).getOrElse(throw new RuntimeException("Exception"))

    for {
      c <- write(answer)
      //d <- send(answer)
      x <- receive()
      _ = println(x)
    } yield ()
  }
  val futureValue =
    program.foldMap(CommandInterpreter.interpreter).unsafeRunAsyncFuture()
  val res = Await.result(futureValue, Duration.Inf)
  println(res)
}
