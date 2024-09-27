/*
 * Copyright (c) 2024 sparetimedevs and respective authors and developers.
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

package com.sparetimedevs.ami.core.validation

import arrow.core.Either
import arrow.core.EitherNel
import arrow.core.NonEmptyList
import arrow.core.right

public fun <T> Iterable<EitherNel<ValidationError, T>>.combineAllValidationErrors():
    EitherNel<ValidationError, List<T>> =
    this.fold(emptyList<T>().right()) {
        acc: Either<NonEmptyList<ValidationError>, List<T>>,
        el: Either<NonEmptyList<ValidationError>, T> ->
        Either.zipOrAccumulate(
            { e1: NonEmptyList<ValidationError>, e2: NonEmptyList<ValidationError> -> e1 + e2 },
            acc,
            el,
            { b1: List<T>, b2: T -> b1 + b2 }
        )
    }
