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

package com.sparetimedevs.ami.music.data.kotlin.note

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.sparetimedevs.ami.core.validation.ValidationError
import com.sparetimedevs.ami.core.validation.getOrThrow
import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

/**
 * The semitones type is a number representing semitones, used for chromatic alteration. A value of
 * -1 corresponds to a flat and a value of 1 to a sharp. Decimal values like 0.5 (quarter tone
 * sharp) are used for microtones.
 */
@Serializable
@JvmInline
public value class Semitones private constructor(public val value: Float) {
    public companion object {

        public val DefaultSemitones: Semitones = Semitones(0.0f)

        public fun validate(input: Float): Either<ValidationError, Semitones> = either {
            // Are these good minimums and maximums?
            ensure(input > -10.0f) {
                ValidationError("Semitones can't be lesser than -10.0, the input was $input")
            }
            ensure(input < 10.0f) {
                ValidationError("Semitones can't be greater than 10.0, the input was $input")
            }
            Semitones(input)
        }

        public fun unsafeCreate(input: Float): Semitones = validate(input).getOrThrow()
    }
}
