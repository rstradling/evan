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

package com.strad.evan.picklers

import io.circe._
import io.circe.parser._

trait Pickler[S] {
  def pickle(data: S): String
}

trait UnPickler[S] {
  def unPickle(s: String): S
}

object JsonPickler {
  def pickle[S](a: S)(implicit s: Pickler[S]) = s.pickle(a)

  implicit val jsonPickle: Pickler[Json] =
    new Pickler[Json] {
      override def pickle(data: Json): String = data.toString()
    }
}

object JsonUnPickler {
  def unPickle[S](str: String)(implicit s: UnPickler[S]) = s.unPickle(str)
  implicit val jsonUnPickle: UnPickler[Json] =
    new UnPickler[Json] {
      override def unPickle(str: String): Json =
        parse(str).getOrElse(throw new RuntimeException("Issue parsing Json"))
    }
}
