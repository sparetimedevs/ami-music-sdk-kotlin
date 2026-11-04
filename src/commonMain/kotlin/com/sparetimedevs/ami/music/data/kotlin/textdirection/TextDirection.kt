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

package com.sparetimedevs.ami.music.data.kotlin.textdirection

import arrow.core.EitherNel
import arrow.core.leftNel
import arrow.core.right
import com.sparetimedevs.ami.core.validation.NoValidationIdentifier
import com.sparetimedevs.ami.core.validation.ValidationError
import com.sparetimedevs.ami.core.validation.ValidationErrorForProperty
import com.sparetimedevs.ami.core.validation.ValidationIdentifier
import com.sparetimedevs.ami.music.data.kotlin.fraction.Fraction
import kotlinx.serialization.Serializable

/** A written direction at [offset] in a measure, such as `2x` or `D.C. al Fine`. */
@Serializable
public data class TextDirection(
    val offset: Fraction,
    val text: String,
) {
    public companion object {
        public const val MAX_TEXT_LENGTH: Int = 512

        /** `text` isn't empty and at most [MAX_TEXT_LENGTH] characters. */
        public fun validateText(
            text: String,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
        ): EitherNel<ValidationError, String> =
            when {
                text.isEmpty() ->
                    ValidationError(
                        "Text direction text can't be empty",
                        ValidationErrorForProperty("TextDirectionText"),
                        validationIdentifier,
                    ).leftNel()
                text.length > MAX_TEXT_LENGTH ->
                    ValidationError(
                        "Text direction text can't be longer than $MAX_TEXT_LENGTH characters, the input was ${text.length} characters",
                        ValidationErrorForProperty("TextDirectionText"),
                        validationIdentifier,
                    ).leftNel()
                else -> text.right()
            }
    }
}
