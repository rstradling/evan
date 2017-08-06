package com.strad.evan.algebra

import cats.InjectK
import cats.free.Free
import io.circe.Json

object Bus {
  sealed trait MessageBusA[A]
  case class Send(item: Json) extends MessageBusA[Unit]
  case class Receive(s: String) extends MessageBusA[Json]
   class MessageBus[F[_]](implicit I: InjectK[MessageBusA, F]) {
    def send(item: Json): Free[F, Unit] =
      Free.inject[MessageBusA, F](Send(item))

    def receive(str: String): Free[F, Json] =
      Free.inject[MessageBusA, F](Receive(str))
  }

  object MessageBus {
    implicit def messageBus[F[_]](
                                   implicit
                                   I: InjectK[MessageBusA, F]
                                 ): MessageBus[F] = new MessageBus[F]
  }

}
