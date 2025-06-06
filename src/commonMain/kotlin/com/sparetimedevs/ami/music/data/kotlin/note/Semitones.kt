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

/**
 * The semitones type is a number representing semitones, used for chromatic alteration. A value of
 * -1 corresponds to a flat and a value of 1 to a sharp. Decimal values like 0.5 (quarter tone
 * sharp) are used for microtones.
 */
@Serializable
@JvmInline
public value class Semitones private constructor(
    public val value: Float,
) {
    public companion object {
        public val DefaultSemitones: Semitones = Semitones(0.0f)

        public fun validate(
            input: Float,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
        ): EitherNel<ValidationError, Semitones> =
            either {
                // Are these good minimums and maximums?
                ensure(input >= -10.0f) {
                    ValidationError(
                        "Semitones can't be lesser than -10.0, the input was $input",
                        validationErrorForProperty<Semitones>(),
                        validationIdentifier,
                    ).nel()
                }
                ensure(input <= 10.0f) {
                    ValidationError(
                        "Semitones can't be greater than 10.0, the input was $input",
                        validationErrorForProperty<Semitones>(),
                        validationIdentifier,
                    ).nel()
                }
                Semitones(input)
            }

        public fun unsafeCreate(input: Float): Semitones =
            validate(
                input,
                NoValidationIdentifier,
            ).getOrThrowFirstValidationError()
    }
}
