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

package com.sparetimedevs.ami.music.data.kotlin.midi

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.sparetimedevs.ami.core.validation.NoValidationIdentifier
import com.sparetimedevs.ami.core.validation.ValidationError
import com.sparetimedevs.ami.core.validation.ValidationErrorFor
import com.sparetimedevs.ami.core.validation.ValidationErrorForUnknown
import com.sparetimedevs.ami.core.validation.ValidationIdentifier
import com.sparetimedevs.ami.core.validation.getOrThrow
import com.sparetimedevs.ami.core.validation.validationErrorForProperty
import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
public value class MidiChannel private constructor(public val value: Byte) {
    public companion object {
        public fun validate(
            input: Byte?,
            validationErrorFor: ValidationErrorFor = ValidationErrorForUnknown,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier
        ): Either<ValidationError, MidiChannel?> = either {
            if (input == null) {
                return@either null
            }
            ensure(input >= 0) {
                ValidationError(
                    "Midi channel can't be negative, the input was $input",
                    validationErrorForProperty<MidiChannel>(),
                    validationErrorFor,
                    validationIdentifier
                )
            }
            MidiChannel(input)
        }

        public fun unsafeCreate(input: Byte): MidiChannel =
            validate(input, ValidationErrorForUnknown, NoValidationIdentifier).getOrThrow()!!
    }
}

@Serializable
@JvmInline
public value class MidiProgram private constructor(public val value: Byte) {
    public companion object {
        public fun validate(
            input: Byte?,
            validationErrorFor: ValidationErrorFor = ValidationErrorForUnknown,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier
        ): Either<ValidationError, MidiProgram?> = either {
            if (input == null) {
                return@either null
            }
            ensure(input >= 0) {
                ValidationError(
                    "Midi program can't be negative, the input was $input",
                    validationErrorForProperty<MidiProgram>(),
                    validationErrorFor,
                    validationIdentifier
                )
            }
            MidiProgram(input)
        }

        public fun unsafeCreate(input: Byte): MidiProgram =
            validate(input, ValidationErrorForUnknown, NoValidationIdentifier).getOrThrow()!!
    }
}
