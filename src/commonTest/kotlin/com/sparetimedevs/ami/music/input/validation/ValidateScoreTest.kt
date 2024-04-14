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

import com.sparetimedevs.ami.music.data.kotlin.score.Score
import com.sparetimedevs.ami.music.example.getExampleScore0
import com.sparetimedevs.ami.music.example.getExampleScore0Input
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec

class ValidateScoreTest :
    StringSpec({
        "validate score should return valid score with valid input" {
            val inputScore: com.sparetimedevs.ami.music.input.Score = getExampleScore0Input()
            val expectedScore: Score = getExampleScore0()

            inputScore.validate() shouldBeRight expectedScore
        }

        // TODO write test for when multiple things do not pass the validation.
    })
