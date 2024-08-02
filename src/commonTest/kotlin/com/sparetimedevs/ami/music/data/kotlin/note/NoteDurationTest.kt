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

import com.sparetimedevs.ami.core.validation.ValidationError
import com.sparetimedevs.ami.core.validation.ValidationErrorForNote
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec

class NoteDurationTest :
    StringSpec({
        val validationErrorFor =
            ValidationErrorForNote(
                scoreId = "99d9",
                partId = "gg25l3",
                measureIndex = 23,
                noteIndex = 12
            )

        "validate should work with valid input for dotted maxima" {
            NoteDuration.validate(12.0, validationErrorFor) shouldBeRight
                NoteDuration(NoteValue.MAXIMA, NoteModifier.DOTTED)
        }

        "validate should work with valid input for octuple dotted maxima" {
            NoteDuration.validate(8.03125, validationErrorFor) shouldBeRight
                NoteDuration(NoteValue.MAXIMA, NoteModifier.OCTUPLE_DOTTED)
        }

        "validate should work with valid input for maxima" {
            NoteDuration.validate(8.0, validationErrorFor) shouldBeRight
                NoteDuration(NoteValue.MAXIMA)
        }

        "validate should work with valid input for 8th" {
            NoteDuration.validate(0.125, validationErrorFor) shouldBeRight
                NoteDuration(NoteValue._8TH)
        }

        "validate should yield validation input for invalid input" {
            NoteDuration.validate(1.23456789, validationErrorFor) shouldBeLeft
                ValidationError(
                    "Input for note duration is not a valid value, the value is: 1.23456789",
                    validationErrorFor
                )
        }
    })
