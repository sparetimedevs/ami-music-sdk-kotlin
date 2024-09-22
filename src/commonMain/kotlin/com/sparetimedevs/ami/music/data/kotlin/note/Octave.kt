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
import com.sparetimedevs.ami.core.validation.NoValidationIdentifier
import com.sparetimedevs.ami.core.validation.ValidationError
import com.sparetimedevs.ami.core.validation.ValidationIdentifier
import com.sparetimedevs.ami.core.validation.getOrThrow
import com.sparetimedevs.ami.core.validation.validationErrorForProperty
import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
public value class Octave private constructor(public val value: Byte) {
    public companion object {

        public fun validate(
            input: Byte,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier
        ): Either<ValidationError, Octave> = either {
            // Are these good minimums and maximums?
            ensure(input > -13f) {
                ValidationError(
                    "Octave can't be lesser than -12, the input was $input",
                    validationErrorForProperty<Octave>(),
                    validationIdentifier
                )
            }
            ensure(input < 13) {
                ValidationError(
                    "Octave can't be greater than 12, the input was $input",
                    validationErrorForProperty<Octave>(),
                    validationIdentifier
                )
            }
            Octave(input)
        }

        public fun unsafeCreate(input: Byte): Octave =
            validate(input, NoValidationIdentifier).getOrThrow()
    }
}
