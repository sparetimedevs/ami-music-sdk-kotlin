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

package com.sparetimedevs.ami.music.data.kotlin.chord

import arrow.core.Either
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

/** An alteration of a chord, e.g. add9: `ChordDegree(9, 0, ADD)`. */
@Serializable public data class ChordDegree(
    val value: DegreeValue,
    val alter: DegreeAlter,
    val type: DegreeType,
) {
    public companion object {
        public fun validate(
            value: Int,
            alter: Int,
            type: String,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
        ): EitherNel<ValidationError, ChordDegree> =
            Either.zipOrAccumulate(
                DegreeValue.validate(value, validationIdentifier),
                DegreeAlter.validate(alter, validationIdentifier),
                DegreeType.validate(type, validationIdentifier),
            ) { degreeValue, degreeAlter, degreeType -> ChordDegree(degreeValue, degreeAlter, degreeType) }
    }
}

/** The chord degree that is added, altered or subtracted, 2…13. */
@Serializable
@JvmInline
public value class DegreeValue private constructor(
    public val value: Byte,
) {
    public companion object {
        public const val MIN: Int = 2
        public const val MAX: Int = 13

        public fun validate(
            input: Int,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
        ): EitherNel<ValidationError, DegreeValue> =
            either {
                ensure(input in MIN..MAX) {
                    ValidationError(
                        "Degree value can't be smaller than $MIN or bigger than $MAX, the input was $input",
                        validationErrorForProperty<DegreeValue>(),
                        validationIdentifier,
                    ).nel()
                }
                DegreeValue(input.toByte())
            }

        public fun unsafeCreate(input: Int): DegreeValue =
            validate(input, NoValidationIdentifier).getOrThrowFirstValidationError()
    }
}

/** The alteration in semitones, −2…2. */
@Serializable
@JvmInline
public value class DegreeAlter private constructor(
    public val value: Byte,
) {
    public companion object {
        public const val MIN: Int = -2
        public const val MAX: Int = 2

        public fun validate(
            input: Int,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
        ): EitherNel<ValidationError, DegreeAlter> =
            either {
                ensure(input in MIN..MAX) {
                    ValidationError(
                        "Degree alter can't be smaller than $MIN or bigger than $MAX, the input was $input",
                        validationErrorForProperty<DegreeAlter>(),
                        validationIdentifier,
                    ).nel()
                }
                DegreeAlter(input.toByte())
            }

        public fun unsafeCreate(input: Int): DegreeAlter =
            validate(input, NoValidationIdentifier).getOrThrowFirstValidationError()
    }
}

@Serializable
public enum class DegreeType {
    ADD,
    ALTER,
    SUBTRACT,
    ;

    public companion object {
        public fun validate(
            input: String,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
        ): EitherNel<ValidationError, DegreeType> =
            when (val type = entries.firstOrNull { it.name == input }) {
                null ->
                    ValidationError(
                        "Degree type can't be value $input",
                        validationErrorForProperty<DegreeType>(),
                        validationIdentifier,
                    ).nel()
                        .left()

                else -> type.right()
            }
    }
}
