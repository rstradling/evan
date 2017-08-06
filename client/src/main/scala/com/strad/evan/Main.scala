package com.strad.evan

import cats.free.Free
import com.strad.evan.App.CqrsApp
import com.strad.evan.dsl.BusDsl.MessageBus
import com.strad.evan.interpreters.event.AppInterpreter
import io.circe._
import io.circe.parser._
import fs2.interop.cats._

object Main extends App {

  import dsl.EventStoreDsl._
  import scala.concurrent._
   implicit val scheduler =
    _root_.fs2.Scheduler.fromFixedDaemonPool(2, "generator-scheduler")
  implicit val S =
    _root_.fs2.Strategy.fromFixedDaemonPool(2, "generator-timer")
 import scala.concurrent.duration._

  def program(implicit
              I: EventStore[CqrsApp],
              B: MessageBus[CqrsApp]): Free[CqrsApp, Unit] = {
    import I._, B._
    val answer: Json = parse("""{
                               |  "data": "yours",
                               |  "mine": "theirs",
                               |  "test": "mine"
                               | }
                             """.stripMargin).getOrElse(throw new RuntimeException("Exception"))

    for {
      c <- write(answer)
      d <- send(answer)
    } yield ()
  }
  val futureValue =
    program.foldMap(AppInterpreter.interpreter).unsafeRunAsyncFuture()
  val res = Await.result(futureValue, Duration.Inf)
  println(res)
}
