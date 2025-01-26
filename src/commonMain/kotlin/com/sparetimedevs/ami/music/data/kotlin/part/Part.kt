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

import arrow.core.EitherNel
import arrow.core.nel
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.sparetimedevs.ami.core.Id
import com.sparetimedevs.ami.core.util.randomUuidString
import com.sparetimedevs.ami.core.validation.NoValidationIdentifier
import com.sparetimedevs.ami.core.validation.ValidationError
import com.sparetimedevs.ami.core.validation.ValidationIdentifier
import com.sparetimedevs.ami.core.validation.getOrThrowFirstValidationError
import com.sparetimedevs.ami.core.validation.validationErrorForProperty
import com.sparetimedevs.ami.music.data.kotlin.measure.Measure
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
public value class PartId private constructor(
    public val value: String,
) : Id {
    public companion object {
        public operator fun invoke(): PartId = PartId(randomUuidString())

        public fun validate(
            input: String,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
        ): EitherNel<ValidationError, PartId> =
            either {
                ensure(input.isNotEmpty()) {
                    ValidationError(
                        "Part ID can't be empty, the input was $input",
                        validationErrorForProperty<PartId>(),
                        validationIdentifier,
                    ).nel()
                }
                ensure(input.length <= 128) {
                    ValidationError(
                        "Part ID can't be longer than 128 characters, the input was $input",
                        validationErrorForProperty<PartId>(),
                        validationIdentifier,
                    ).nel()
                }
                PartId(input)
            }

        public fun unsafeCreate(input: String): PartId =
            validate(
                input,
                NoValidationIdentifier,
            ).getOrThrowFirstValidationError()
    }
}

@Serializable
@JvmInline
public value class PartName private constructor(
    public val value: String,
) {
    public companion object {
        public fun validate(
            input: String?,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
        ): EitherNel<ValidationError, PartName?> =
            either {
                if (input.isNullOrEmpty()) {
                    return@either null
                }
                ensure(input.length <= 512) {
                    ValidationError(
                        "Part name can't be longer than 512 characters, the input was $input",
                        validationErrorForProperty<PartName>(),
                        validationIdentifier,
                    ).nel()
                }
                PartName(input)
            }

        public fun unsafeCreate(input: String): PartName =
            validate(
                input,
                NoValidationIdentifier,
            ).getOrThrowFirstValidationError()!!
    }
}

@Serializable
public data class Part(
    val id: PartId,
    val name: PartName? = null,
    val instrument: PartInstrument? = null,
    val measures: List<Measure>,
)
