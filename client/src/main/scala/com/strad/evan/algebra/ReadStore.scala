package com.strad.evan.algebra

import cats.InjectK
import cats.free.Free
import io.circe.Json

object ReadStore{
  sealed trait ReadStoreA[A]
  case class Write(item: Json) extends ReadStoreA[Unit]

  class ReadStore[F[_]](implicit I: InjectK[ReadStoreA, F]) {
    def write(data: Json): Free[F, Unit] =
      Free.inject[ReadStoreA, F](Write(data))
  }

  object ReadStore {
    implicit def readStore[F[_]](
                                   implicit
                                   I: InjectK[ReadStoreA, F]
                                 ): ReadStore[F] = new ReadStore[F]
  }
}
