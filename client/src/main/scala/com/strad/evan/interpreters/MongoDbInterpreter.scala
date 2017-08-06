package com.strad.evan.interpreters

import cats.~>
import com.strad.evan.algebra.EventStore.{EventStoreA, Write}
import fs2.Task
import org.mongodb.scala._

object MongoDbInterpreter extends (EventStoreA ~> Task) {
  implicit val scheduler =
    _root_.fs2.Scheduler.fromFixedDaemonPool(2, "generator-scheduler")
  implicit val S =
    _root_.fs2.Strategy.fromFixedDaemonPool(2, "generator-timer")
  val mc = MongoClient()
  val db = mc.getDatabase("mydb")
  val c = db.getCollection("test")

  def apply[A](fa: EventStoreA[A]): Task[A] =
    fa match {
      case Write(item) =>
        val doc = Document(item.toString)
        Task
          .fromFuture(c.insertOne(doc).toFuture)(
            S,
            scala.concurrent.ExecutionContext.Implicits.global
          )
          .map(x => ())
    }
}

