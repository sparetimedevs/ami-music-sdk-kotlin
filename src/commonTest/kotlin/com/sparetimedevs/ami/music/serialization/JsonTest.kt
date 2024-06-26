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

package com.sparetimedevs.ami.music.serialization

import arrow.core.getOrElse
import com.sparetimedevs.ami.music.data.kotlin.score.Score
import com.sparetimedevs.ami.music.example.getExampleScore0
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.StringSpec
import kotlinx.serialization.json.Json

class JsonTest :
    StringSpec({
        val jsonParser = Json

        "fromJson should work with valid input" {
            val expectedScore: Score = getExampleScore0()
            val input =
                """{"id":"d737b4ae-fbaa-4b0d-9d36-d3651e30e93a","parts":[{"id":"p-1","measures":[{"notes":[{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"pitch":{"noteName":"C","alter":0,"octave":4},"noteAttributes":{}},{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"pitch":{"noteName":"G","alter":0,"octave":4},"noteAttributes":{}},{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"pitch":{"noteName":"A","alter":0,"octave":4},"noteAttributes":{}},{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"pitch":{"noteName":"E","alter":0,"octave":4},"noteAttributes":{}}]},{"notes":[{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"pitch":{"noteName":"C","alter":0,"octave":4},"noteAttributes":{}},{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"pitch":{"noteName":"C","alter":0,"octave":4},"noteAttributes":{}},{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"pitch":{"noteName":"A","alter":0,"octave":4},"noteAttributes":{}},{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"pitch":{"noteName":"A","alter":0,"octave":4},"noteAttributes":{}}]}]},{"id":"p-2","measures":[{"notes":[{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"pitch":{"noteName":"C","alter":0,"octave":4},"noteAttributes":{}},{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"pitch":{"noteName":"G","alter":0,"octave":4},"noteAttributes":{}},{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"pitch":{"noteName":"A","alter":0,"octave":4},"noteAttributes":{}},{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"pitch":{"noteName":"E","alter":0,"octave":4},"noteAttributes":{}}]},{"notes":[{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"pitch":{"noteName":"C","alter":0,"octave":4},"noteAttributes":{}},{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"pitch":{"noteName":"C","alter":0,"octave":4},"noteAttributes":{}},{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"pitch":{"noteName":"A","alter":0,"octave":4},"noteAttributes":{}},{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"pitch":{"noteName":"A","alter":0,"octave":4},"noteAttributes":{}}]}]}]}"""
                    .trimIndent()

            fromJson(jsonParser, input) shouldBeRight expectedScore
        }

        "toJson should produce JSON" {
            val score: Score = getExampleScore0()
            val expectedJson =
                """{"id":"d737b4ae-fbaa-4b0d-9d36-d3651e30e93a","parts":[{"id":"p-1","measures":[{"attributes":{},"notes":[{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"noteAttributes":{},"pitch":{"alter":0.0,"noteName":"C","octave":4}},{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"noteAttributes":{},"pitch":{"alter":0.0,"noteName":"G","octave":4}},{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"noteAttributes":{},"pitch":{"alter":0.0,"noteName":"A","octave":4}},{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"noteAttributes":{},"pitch":{"alter":0.0,"noteName":"E","octave":4}}]},{"attributes":{},"notes":[{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"noteAttributes":{},"pitch":{"alter":0.0,"noteName":"C","octave":4}},{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"noteAttributes":{},"pitch":{"alter":0.0,"noteName":"C","octave":4}},{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"noteAttributes":{},"pitch":{"alter":0.0,"noteName":"A","octave":4}},{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"noteAttributes":{},"pitch":{"alter":0.0,"noteName":"A","octave":4}}]}]},{"id":"p-2","measures":[{"attributes":{},"notes":[{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"noteAttributes":{},"pitch":{"alter":0.0,"noteName":"C","octave":4}},{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"noteAttributes":{},"pitch":{"alter":0.0,"noteName":"G","octave":4}},{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"noteAttributes":{},"pitch":{"alter":0.0,"noteName":"A","octave":4}},{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"noteAttributes":{},"pitch":{"alter":0.0,"noteName":"E","octave":4}}]},{"attributes":{},"notes":[{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"noteAttributes":{},"pitch":{"alter":0.0,"noteName":"C","octave":4}},{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"noteAttributes":{},"pitch":{"alter":0.0,"noteName":"C","octave":4}},{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"noteAttributes":{},"pitch":{"alter":0.0,"noteName":"A","octave":4}},{"duration":{"noteValue":"QUARTER","modifier":"NONE"},"noteAttributes":{},"pitch":{"alter":0.0,"noteName":"A","octave":4}}]}]}]}"""
                    .trimIndent()

            toJson(jsonParser, score).getOrElse {
                throw RuntimeException("Test failed")
            } shouldEqualJson expectedJson
        }
    })
