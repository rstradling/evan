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
import fs2.Task
import fs2.interop.cats._
import io.circe.Json

object Algebra {

  trait Serialize[S] {
    def serialize(data: S): String
  }

  object Serialize {
    def serialize[S](a: S)(implicit s: Serialize[S]) = s.serialize(a)

    implicit val jsonSerialize: Serialize[Json] =
      new Serialize[Json] {
        override def serialize(data: Json): String = data.toString()
      }
  }


  sealed trait EventStoreA[A]

  case class Write[T](item: T, ev: Serialize[T]) extends EventStoreA[Unit]

  import cats.free.Free
  import cats.free.Free.liftF

  type EventStore[A] = Free[EventStoreA, A]
  def write[T](item: T)(implicit s: Serialize[T]): EventStore[Unit] =
    liftF[EventStoreA, Unit](Write[T](item, s))


  object ErrorOrObj {

    sealed trait Error extends Product with Serializable

    case class ParserError(s: String) extends Error

    case class ConnectionError(s: String) extends Error

    type ErrorOr[A] = Either[Error, A]
  }

}

object Main extends App {
  import Algebra._
  implicit val scheduler = _root_.fs2.Scheduler.fromFixedDaemonPool(2, "generator-scheduler")
  implicit val S         = _root_.fs2.Strategy.fromFixedDaemonPool(2, "generator-timer")
  import Algebra.Serialize._

  def program: EventStore[Unit] = {
    for {
      c <- write(item)
    } yield ()
  }

  import cats.arrow.FunctionK
  import cats.~>
  def compiler: EventStoreA ~> _root_.fs2.Task =
    new (EventStoreA ~> _root_.fs2.Task) {
      def apply[A](fa: EventStoreA[A]): Task[A] =
        fa match {
          case Write(item, ev) =>
            val data = ev.serialize(item)
            Task(())
        }
    }

  import cats.instances.future._
  import scala.concurrent._
  import scala.concurrent.duration._
  import scala.concurrent.ExecutionContext.Implicits.global

  val item: Json = Json.fromBoolean(true)
  val futureValue = program.foldMap(compiler).unsafeRunAsyncFuture()
  val res = Await.result(futureValue, Duration.Inf)
  println(res)
}
