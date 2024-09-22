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

package com.sparetimedevs.ami.music.data.kotlin.score

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.sparetimedevs.ami.core.util.randomUuidString
import com.sparetimedevs.ami.core.validation.NoValidationIdentifier
import com.sparetimedevs.ami.core.validation.ValidationError
import com.sparetimedevs.ami.core.validation.ValidationIdentifier
import com.sparetimedevs.ami.core.validation.getOrThrow
import com.sparetimedevs.ami.core.validation.validationErrorForProperty
import com.sparetimedevs.ami.music.data.kotlin.part.Part
import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
public value class ScoreId private constructor(public val value: String) {
    public companion object {

        public operator fun invoke(): ScoreId = ScoreId(randomUuidString())

        public fun validate(
            input: String,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier
        ): Either<ValidationError, ScoreId> = either {
            ensure(input.isNotEmpty()) {
                ValidationError(
                    "Score ID can't be empty, the input was $input",
                    validationErrorForProperty<ScoreId>(),
                    validationIdentifier
                )
            }
            ensure(input.length <= 128) {
                ValidationError(
                    "Score ID can't be longer than 128 characters, the input was $input",
                    validationErrorForProperty<ScoreId>(),
                    validationIdentifier
                )
            }
            ScoreId(input)
        }

        public fun unsafeCreate(input: String): ScoreId =
            validate(input, NoValidationIdentifier).getOrThrow()
    }
}

@Serializable
@JvmInline
public value class ScoreTitle private constructor(public val value: String) {
    public companion object {

        public fun validate(
            input: String,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier
        ): Either<ValidationError, ScoreTitle> = either {
            ensure(input.isNotEmpty()) {
                ValidationError(
                    "Score title can't be empty, the input was $input",
                    validationErrorForProperty<ScoreTitle>(),
                    validationIdentifier
                )
            }
            ensure(input.length < 513) {
                ValidationError(
                    "Score title can't be longer than 512 characters, the input was $input",
                    validationErrorForProperty<ScoreTitle>(),
                    validationIdentifier
                )
            }
            ScoreTitle(input)
        }

        public fun unsafeCreate(input: String): ScoreTitle =
            validate(input, NoValidationIdentifier).getOrThrow()
    }
}

@Serializable
public data class Score(val id: ScoreId, val title: ScoreTitle?, val parts: List<Part>)
