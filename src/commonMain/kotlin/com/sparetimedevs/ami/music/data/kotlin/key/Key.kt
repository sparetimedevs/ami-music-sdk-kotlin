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

package com.sparetimedevs.ami.music.data.kotlin.key

import arrow.core.EitherNel
import arrow.core.left
import arrow.core.nel
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.right
import com.sparetimedevs.ami.core.validation.NoValidationIdentifier
import com.sparetimedevs.ami.core.validation.ValidationError
import com.sparetimedevs.ami.core.validation.ValidationIdentifier
import com.sparetimedevs.ami.core.validation.getOrThrowFirstValidationError
import com.sparetimedevs.ami.core.validation.validationErrorForProperty
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/** A key signature, as in MusicXML: [fifths] is the number of sharps (positive) or flats (negative). */
@Serializable public data class Key(
    val fifths: Fifths,
    val mode: KeyMode,
)

/** The number of sharps (positive) or flats (negative), −7…7. C major and A minor are 0. */
@Serializable
@JvmInline
public value class Fifths private constructor(
    public val value: Byte,
) {
    public companion object {
        public const val MIN: Int = -7
        public const val MAX: Int = 7

        public fun validate(
            input: Int,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
        ): EitherNel<ValidationError, Fifths> =
            either {
                ensure(input in MIN..MAX) {
                    ValidationError(
                        "Fifths can't be smaller than $MIN or bigger than $MAX, the input was $input",
                        validationErrorForProperty<Fifths>(),
                        validationIdentifier,
                    ).nel()
                }
                Fifths(input.toByte())
            }

        public fun unsafeCreate(input: Int): Fifths =
            validate(input, NoValidationIdentifier).getOrThrowFirstValidationError()
    }
}

@Serializable
public enum class KeyMode {
    MAJOR,
    MINOR,
    ;

    public companion object {
        public fun validate(
            input: String,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
        ): EitherNel<ValidationError, KeyMode> =
            when (input) {
                MAJOR.name -> MAJOR.right()
                MINOR.name -> MINOR.right()
                else ->
                    ValidationError(
                        "Key mode can't be value $input",
                        validationErrorForProperty<KeyMode>(),
                        validationIdentifier,
                    ).nel()
                        .left()
            }
    }
}
