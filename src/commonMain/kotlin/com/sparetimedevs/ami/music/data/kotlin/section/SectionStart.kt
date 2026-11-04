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

package com.sparetimedevs.ami.music.data.kotlin.section

import arrow.core.EitherNel
import arrow.core.leftNel
import arrow.core.right
import com.sparetimedevs.ami.core.validation.NoValidationIdentifier
import com.sparetimedevs.ami.core.validation.ValidationError
import com.sparetimedevs.ami.core.validation.ValidationErrorForProperty
import com.sparetimedevs.ami.core.validation.ValidationIdentifier
import com.sparetimedevs.ami.music.data.kotlin.fraction.Fraction
import kotlinx.serialization.Serializable

/** The start of a new section of the song, such as a verse or a chorus, at [offset] (D15). */
@Serializable
public data class SectionStart(
    val offset: Fraction,
    val label: String? = null,
) {
    public companion object {
        public const val MAX_LABEL_LENGTH: Int = 64

        /** A `label`, when present, isn't empty after trimming and is at most [MAX_LABEL_LENGTH] characters. */
        public fun validateLabel(
            label: String?,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
        ): EitherNel<ValidationError, String?> =
            when {
                label == null -> null.right()
                label.isBlank() ->
                    ValidationError(
                        "Section label can't be empty",
                        ValidationErrorForProperty("SectionStartLabel"),
                        validationIdentifier,
                    ).leftNel()
                label.length > MAX_LABEL_LENGTH ->
                    ValidationError(
                        "Section label can't be longer than $MAX_LABEL_LENGTH characters, the input was ${label.length} characters",
                        ValidationErrorForProperty("SectionStartLabel"),
                        validationIdentifier,
                    ).leftNel()
                else -> label.right()
            }
    }
}
