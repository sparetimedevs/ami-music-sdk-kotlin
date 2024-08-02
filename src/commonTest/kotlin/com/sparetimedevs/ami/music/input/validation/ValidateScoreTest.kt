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

package com.sparetimedevs.ami.music.input.validation

import com.sparetimedevs.ami.core.validation.ValidationError
import com.sparetimedevs.ami.core.validation.ValidationErrorForNote
import com.sparetimedevs.ami.music.data.kotlin.score.Score
import com.sparetimedevs.ami.music.example.getExampleScore0
import com.sparetimedevs.ami.music.example.getExampleScore0Input
import com.sparetimedevs.ami.music.example.getExampleScore0InvalidInput
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec

class ValidateScoreTest :
    StringSpec({
        "validate score should return valid score with valid input" {
            val inputScore: com.sparetimedevs.ami.music.input.Score = getExampleScore0Input()
            val expectedScore: Score = getExampleScore0()

            inputScore.validate(
                validationErrorFor = null /* null because it doesn't have a parent */
            ) shouldBeRight expectedScore
        }

        "validate score should return validation errors with invalid input" {
            val inputScore: com.sparetimedevs.ami.music.input.Score = getExampleScore0InvalidInput()
            val expectedValidationErrors =
                listOf(
                    ValidationError(
                        message = "Note value can't be value QUARTAAAR",
                        validationErrorFor =
                            ValidationErrorForNote(
                                "d737b4ae-fbaa-4b0d-9d36-d3651e30e93a",
                                "p-1",
                                0,
                                0
                            )
                    ),
                    ValidationError(
                        message = "Octave can't be greater than 12, the input was 127",
                        validationErrorFor =
                            ValidationErrorForNote(
                                "d737b4ae-fbaa-4b0d-9d36-d3651e30e93a",
                                "p-1",
                                0,
                                1
                            )
                    ),
                    ValidationError(
                        message = "Note name can't be value L",
                        validationErrorFor =
                            ValidationErrorForNote(
                                "d737b4ae-fbaa-4b0d-9d36-d3651e30e93a",
                                "p-1",
                                0,
                                3
                            )
                    )
                )

            inputScore.validate(
                validationErrorFor = null /* null because it doesn't have a parent */
            ) shouldBeLeft expectedValidationErrors
        }
    })
