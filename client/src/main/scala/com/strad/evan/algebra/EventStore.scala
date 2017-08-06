package com.strad.evan.algebra

import cats.free.Free
import cats.InjectK
import io.circe.Json

object EventStore {
  sealed trait EventStoreA[A]
  case class Write(item: Json) extends EventStoreA[Unit]

  class EventStore[F[_]](implicit I: InjectK[EventStoreA, F]) {
    def write(data: Json): Free[F, Unit] =
      Free.inject[EventStoreA, F](Write(data))
  }

  object EventStore {
    implicit def eventStore[F[_]](
                                   implicit
                                   I: InjectK[EventStoreA, F]
                                 ): EventStore[F] = new EventStore[F]
  }
}
