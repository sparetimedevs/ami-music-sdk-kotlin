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

package com.sparetimedevs.ami.music.data.kotlin.tempo

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

/** The playback tempo in quarter notes per minute, whatever the time signature (as MusicXML's `<sound tempo>`). */
@Serializable public data class Tempo(
    val quarterNotesPerMinute: QuarterNotesPerMinute,
)

/** Quarter notes per minute, 20…400. */
@Serializable
@JvmInline
public value class QuarterNotesPerMinute private constructor(
    public val value: Short,
) {
    public companion object {
        public const val MIN: Int = 20
        public const val MAX: Int = 400

        public fun validate(
            input: Int,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
        ): EitherNel<ValidationError, QuarterNotesPerMinute> =
            either {
                ensure(input in MIN..MAX) {
                    ValidationError(
                        "Quarter notes per minute can't be smaller than $MIN or bigger than $MAX, the input was $input",
                        validationErrorForProperty<QuarterNotesPerMinute>(),
                        validationIdentifier,
                    ).nel()
                }
                QuarterNotesPerMinute(input.toShort())
            }

        public fun unsafeCreate(input: Int): QuarterNotesPerMinute =
            validate(input, NoValidationIdentifier).getOrThrowFirstValidationError()
    }
}
