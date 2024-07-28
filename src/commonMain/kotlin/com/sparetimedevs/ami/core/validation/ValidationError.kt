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

package com.sparetimedevs.ami.core.validation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

public data class ValidationError(
    val message: String = "There was an error while validating the input.",
    val forId: ValidationErrorForId =
        ValidationErrorForId.unsafeCreate(
            "TODO remove this, only hear so compilation works until all is addressed."
        )
)

@Serializable
@JvmInline
public value class ValidationErrorForId private constructor(public val value: String) {
    public companion object {

        public fun validate(input: String): Either<ValidationError, ValidationErrorForId> = either {
            ensure(input.isNotEmpty()) {
                ValidationError("ValidationErrorForId can't be empty, the input was $input")
            }
            ensure(input.length < 1025) {
                ValidationError(
                    "ValidationErrorForId can't be longer than 1024 characters, the input was $input"
                )
            }
            ValidationErrorForId(input)
        }

        public fun unsafeCreate(input: String): ValidationErrorForId = validate(input).getOrThrow()
    }
}
