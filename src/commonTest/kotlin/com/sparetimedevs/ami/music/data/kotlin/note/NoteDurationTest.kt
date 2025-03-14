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

package com.sparetimedevs.ami.music.data.kotlin.note

import arrow.core.nel
import com.sparetimedevs.ami.core.validation.NoValidationIdentifier
import com.sparetimedevs.ami.core.validation.ValidationError
import com.sparetimedevs.ami.core.validation.validationErrorForProperty
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import kotlin.test.Test

class NoteDurationTest {
    @Test
    fun `validate should work with valid input for dotted maxima`() {
        NoteDuration.validate(12.0) shouldBeRight
            NoteDuration(NoteValue.MAXIMA, NoteModifier.DOTTED)
    }

    @Test
    fun `validate should work with valid input for octuple dotted maxima`() {
        NoteDuration.validate(8.03125) shouldBeRight
            NoteDuration(NoteValue.MAXIMA, NoteModifier.OCTUPLE_DOTTED)
    }

    @Test
    fun `validate should work with valid input for maxima`() {
        NoteDuration.validate(8.0) shouldBeRight NoteDuration(NoteValue.MAXIMA)
    }

    @Test
    fun `validate should work with valid input for 8th`() {
        NoteDuration.validate(0.125) shouldBeRight NoteDuration(NoteValue._8TH)
    }

    @Test
    fun `validate should yield validation error for invalid value`() {
        NoteDuration.validate(1.23456789) shouldBeLeft
            ValidationError(
                "Input for note duration is not a valid value, the value is: 1.23456789",
                validationErrorForProperty<NoteDuration>(),
                NoValidationIdentifier,
            ).nel()
    }
}
