/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.strad.evan

import cats.MonadError
import freestyle._
import freestyle.implicits._
import freestyle.fs2._
import freestyle.fs2.implicits._
import io.circe.Json

@free
trait EventStore {
  def write(item: Json): FS[Unit]
}

object ErrorOrObj {
  sealed trait Error extends Product with Serializable
  case class ParserError(s: String) extends Error
  case class ConnectionError(s: String) extends Error
  type ErrorOr[A] = Either[Error, A]
}

@module
trait Cqrs {
  val eventStore: EventStore
  val streams: StreamM
}

object Main extends App {
  implicit val scheduler = _root_.fs2.Scheduler.fromFixedDaemonPool(2, "generator-scheduler")
  implicit val S         = _root_.fs2.Strategy.fromFixedDaemonPool(2, "generator-timer")

  implicit def eventStoreHandler[F[_]](
                                      implicit ME: MonadError[F, Throwable]): EventStore.Handler[F] = new EventStore.Handler[F] {
    override def write(item: Json): F[Unit] = {
      ME.pure(())
    }
  }
  def program[F[_]](implicit app: Cqrs[F]): FreeS[F, Unit] = {
    val item = Json.fromBoolean(true)
    for {
      c <- app.eventStore.write(item)
    } yield ()
  }
  import cats.instances.future._
  import scala.concurrent._
  import scala.concurrent.duration._
  import scala.concurrent.ExecutionContext.Implicits.global

  val futureValue = program[Cqrs.Op].interpret[Future]
  val res = Await.result(futureValue, Duration.Inf)
  println(res)
}
