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

import arrow.core.Either
import com.sparetimedevs.ami.compat.AbstractCompatibilityTest
import com.sparetimedevs.ami.core.DomainError
import com.sparetimedevs.ami.music.data.kotlin.score.Score
import com.sparetimedevs.ami.music.example.getExampleScore0
import com.sparetimedevs.ami.music.example.getExampleScoreHeighHoNobodyHome
import kotlinx.serialization.json.Json

/**
 * Compatibility tests for SDK version 0.0.1-SNAPSHOT.
 *
 * Reading its own frozen JSON fixtures is the baseline check. Reading JSON written by newer SDK
 * versions (newer fixtures and the current examples) tests the forward compatibility direction:
 * an older SDK can deserialize JSON written by newer SDK versions.
 */
class CompatibilityTest : AbstractCompatibilityTest<DomainError, Score>() {
    override fun examples(): Map<String, Score> =
        mapOf(
            // Baseline: v0.0.1 SDK reads v0.0.1 JSON.
            "fixtures/v0.0.1/Score_d737b4ae-fbaa-4b0d-9d36-d3651e30e93a.json" to getExampleScore0(),
            "fixtures/v0.0.1/Score_1064db99-3726-43d7-b0ed-3fc0281bfc02.json" to getExampleScoreHeighHoNobodyHome(),
            // Forward compatibility: v0.0.1 SDK reads JSON written by newer versions.
            "fixtures/v0.0.2/Score_d737b4ae-fbaa-4b0d-9d36-d3651e30e93a.json" to getExampleScore0(),
            "fixtures/v0.0.2/Score_1064db99-3726-43d7-b0ed-3fc0281bfc02.json" to getExampleScoreHeighHoNobodyHome(),
            "../openapi/examples/Score_d737b4ae-fbaa-4b0d-9d36-d3651e30e93a.json" to getExampleScore0(),
            "../openapi/examples/Score_1064db99-3726-43d7-b0ed-3fc0281bfc02.json" to getExampleScoreHeighHoNobodyHome(),
        )

    override fun fromJson(
        jsonParser: Json,
        input: String,
    ): Either<DomainError, Score> =
        com.sparetimedevs.ami.music.serialization
            .fromJson(jsonParser, input)

    override fun toJson(
        jsonParser: Json,
        value: Score,
    ): Either<DomainError, String> =
        com.sparetimedevs.ami.music.serialization
            .toJson(jsonParser, value)
}
