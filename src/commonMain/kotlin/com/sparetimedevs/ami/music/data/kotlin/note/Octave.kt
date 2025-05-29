/*
 * Copyright (c) 2024-2025 sparetimedevs and respective authors and developers.
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

import arrow.core.EitherNel
import arrow.core.nel
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.sparetimedevs.ami.core.validation.NoValidationIdentifier
import com.sparetimedevs.ami.core.validation.ValidationError
import com.sparetimedevs.ami.core.validation.ValidationIdentifier
import com.sparetimedevs.ami.core.validation.getOrThrowFirstValidationError
import com.sparetimedevs.ami.core.validation.validationErrorForProperty
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
public value class Octave private constructor(
    public val value: Byte,
) {
    public companion object {
        public fun validate(
            input: Int,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
        ): EitherNel<ValidationError, Octave> =
            either {
                // Are these good minimums and maximums?
                ensure(input >= -12f) {
                    ValidationError(
                        "Octave can't be lesser than -12, the input was $input",
                        validationErrorForProperty<Octave>(),
                        validationIdentifier,
                    ).nel()
                }
                ensure(input <= 12) {
                    ValidationError(
                        "Octave can't be greater than 12, the input was $input",
                        validationErrorForProperty<Octave>(),
                        validationIdentifier,
                    ).nel()
                }
                Octave(input.toByte())
            }

        public fun unsafeCreate(input: Byte): Octave =
            validate(
                input.toInt(),
                NoValidationIdentifier,
            ).getOrThrowFirstValidationError()
    }
}
