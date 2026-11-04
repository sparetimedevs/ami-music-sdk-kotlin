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

package com.sparetimedevs.ami.music.data.kotlin.lyric

import arrow.core.EitherNel
import arrow.core.leftNel
import arrow.core.right
import com.sparetimedevs.ami.core.validation.NoValidationIdentifier
import com.sparetimedevs.ami.core.validation.ValidationError
import com.sparetimedevs.ami.core.validation.ValidationErrorForProperty
import com.sparetimedevs.ami.core.validation.ValidationIdentifier
import com.sparetimedevs.ami.music.data.kotlin.fraction.Fraction
import kotlinx.serialization.Serializable

/**
 * A piece of lyrics at [offset] in a measure, for [verse] (D7). [text] is kept exactly as written, including leading and
 * trailing spaces.
 */
@Serializable
public data class LyricFragment(
    val offset: Fraction,
    val text: String,
    val verse: Int = FIRST_VERSE,
) {
    public companion object {
        public const val FIRST_VERSE: Int = 1
        public const val MAX_VERSE: Int = 99
        public const val MAX_TEXT_LENGTH: Int = 512

        /** `text` isn't empty and at most [MAX_TEXT_LENGTH] characters. */
        public fun validateText(
            text: String,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
        ): EitherNel<ValidationError, String> =
            when {
                text.isEmpty() ->
                    ValidationError(
                        "Lyric text can't be empty",
                        ValidationErrorForProperty("LyricText"),
                        validationIdentifier,
                    ).leftNel()
                text.length > MAX_TEXT_LENGTH ->
                    ValidationError(
                        "Lyric text can't be longer than $MAX_TEXT_LENGTH characters, the input was ${text.length} characters",
                        ValidationErrorForProperty("LyricText"),
                        validationIdentifier,
                    ).leftNel()
                else -> text.right()
            }

        /** `verse` is 1…[MAX_VERSE]; absent means verse 1. */
        public fun validateVerse(
            verse: Int?,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
        ): EitherNel<ValidationError, Int> =
            when {
                verse == null -> FIRST_VERSE.right()
                verse !in FIRST_VERSE..MAX_VERSE ->
                    ValidationError(
                        "Verse must be between $FIRST_VERSE and $MAX_VERSE, the input was $verse",
                        ValidationErrorForProperty("LyricVerse"),
                        validationIdentifier,
                    ).leftNel()
                else -> verse.right()
            }
    }
}
