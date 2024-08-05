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

package com.sparetimedevs.ami.music.data.kotlin.part

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.sparetimedevs.ami.core.util.randomUuidString
import com.sparetimedevs.ami.core.validation.NoValidationIdentifier
import com.sparetimedevs.ami.core.validation.ValidationError
import com.sparetimedevs.ami.core.validation.ValidationErrorFor
import com.sparetimedevs.ami.core.validation.ValidationErrorForUnknown
import com.sparetimedevs.ami.core.validation.ValidationIdentifier
import com.sparetimedevs.ami.core.validation.getOrThrow
import com.sparetimedevs.ami.music.data.kotlin.measure.Measure
import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
public value class PartId private constructor(public val value: String) {
    public companion object {

        public operator fun invoke(): PartId = PartId(randomUuidString())

        public fun validate(
            input: String,
            validationErrorFor: ValidationErrorFor = ValidationErrorForUnknown,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier
        ): Either<ValidationError, PartId> = either {
            ensure(input.isNotEmpty()) {
                ValidationError(
                    "Part ID can't be empty, the input was $input",
                    validationErrorFor,
                    validationIdentifier
                )
            }
            ensure(input.length <= 128) {
                ValidationError(
                    "Part ID can't be longer than 128 characters, the input was $input",
                    validationErrorFor,
                    validationIdentifier
                )
            }
            PartId(input)
        }

        public fun unsafeCreate(input: String): PartId =
            validate(input, ValidationErrorForUnknown, NoValidationIdentifier).getOrThrow()
    }
}

@Serializable public data class Part(val id: PartId, val measures: List<Measure>)
