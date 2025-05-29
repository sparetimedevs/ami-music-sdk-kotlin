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

package com.sparetimedevs.ami.music.example

import arrow.core.getOrElse
import com.sparetimedevs.ami.music.serialization.fromJson
import com.sparetimedevs.ami.music.serialization.toJson
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.json.shouldEqualJson
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.Test

/**
 * In these tests we are verifying if the examples in Kotlin code are still in line with the examples in JSON.
 */
@Suppress("ktlint:standard:max-line-length")
class ExamplesTest {
    val jsonParser = Json.Default

    val mapOfJsonAndKotlinExamples = mapOf(
        "openapi/examples/Score_d737b4ae-fbaa-4b0d-9d36-d3651e30e93a.json" to getExampleScore0(),
        "openapi/examples/Score_1064db99-3726-43d7-b0ed-3fc0281bfc02.json" to getExampleScoreHeighHoNobodyHome(),
    )

    @Test
    fun `fromJson and toJson should work with examples in JSON and Kotlin code`() {
        mapOfJsonAndKotlinExamples.forEach { (jsonExamplePath, score) ->
            val path = Paths.get(jsonExamplePath)
            val json = Files.readString(path)

            fromJson(jsonParser, json) shouldBeRight score
            toJson(jsonParser, score).getOrElse {
                throw RuntimeException("Test failed")
            } shouldEqualJson json
        }
    }
}
