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

package com.sparetimedevs.ami

import arrow.core.getOrElse
import com.sparetimedevs.ami.music.example.getExampleScore0
import com.sparetimedevs.ami.music.serialization.fromJson
import com.sparetimedevs.ami.music.serialization.toJson
import io.kotest.assertions.arrow.core.shouldBeRight
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test

class AmiMusicSdkTest {
    @Test
    fun `json should ignore unknown fields at score and part and measure level`() {
        val score = getExampleScore0()
        val canonical = toJson(AmiMusicSdk.json, score).getOrElse { throw RuntimeException("Test failed") }
        val withUnknownFields =
            AmiMusicSdk.json
                .parseToJsonElement(canonical)
                .addUnknownFields()
                .toString()

        fromJson(AmiMusicSdk.json, withUnknownFields) shouldBeRight score
    }

    private fun JsonElement.addUnknownFields(): JsonObject {
        val score = jsonObject
        return JsonObject(
            score +
                ("unknownScoreField" to JsonPrimitive("x")) +
                (
                    "parts" to
                        JsonArray(
                            score.getValue("parts").jsonArray.map { part ->
                                val partObject = part.jsonObject
                                JsonObject(
                                    partObject +
                                        ("unknownPartField" to JsonPrimitive(1)) +
                                        (
                                            "measures" to
                                                JsonArray(
                                                    partObject.getValue("measures").jsonArray.map { measure ->
                                                        JsonObject(
                                                            measure.jsonObject +
                                                                (
                                                                    "unknownMeasureField" to
                                                                        buildJsonObject { put("nested", true) }
                                                                ),
                                                        )
                                                    },
                                                )
                                        ),
                                )
                            },
                        )
                ),
        )
    }
}
