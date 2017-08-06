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

import cats.data.{EitherK}
import com.strad.evan.algebra.Bus.MessageBusA
import com.strad.evan.algebra.EventStore.EventStoreA
import com.strad.evan.algebra.ReadStore.ReadStoreA

object App {
  type CommandApp[A] = EitherK[EventStoreA, MessageBusA, A]
  type ReadStoreApp[A] = EitherK[MessageBusA, ReadStoreA, A]
}
