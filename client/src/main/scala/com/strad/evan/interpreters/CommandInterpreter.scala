package com.strad.evan.interpreters

import cats.{ ~> }
import com.strad.evan.App.CommandApp
import fs2.Task

object CommandInterpreter {
  val interpreter: CommandApp ~> Task = MongoDbInterpreter or RabbitMqInterpreter
}

