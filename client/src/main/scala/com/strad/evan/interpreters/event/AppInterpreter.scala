package com.strad.evan.interpreters.event

import cats.{ ~> }
import com.strad.evan.App.CqrsApp
import com.strad.evan.interpreters.event.bus.RabbitMqInterpreter
import com.strad.evan.interpreters.event.store.MongoDbInterpreter
import fs2.Task

object AppInterpreter {
  val interpreter: CqrsApp ~> Task = MongoDbInterpreter or RabbitMqInterpreter
}

