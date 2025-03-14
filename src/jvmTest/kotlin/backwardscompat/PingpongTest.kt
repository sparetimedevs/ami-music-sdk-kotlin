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

package backwardscompat

import arrow.core.flatMap
import com.sparetimedevs.ami.music.data.kotlin.score.Score
import com.sparetimedevs.ami.music.example.getExampleScore0
import com.sparetimedevs.ami.music.input.toInput
import com.sparetimedevs.ami.music.input.validation.validateInput
import io.kotest.assertions.arrow.core.shouldBeRight
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import kotlin.test.Test
import com.sparetimedevs.ami.music.input.Score as InputScore

class PingPongTest {
    val httpClient = OkHttpClient()
    val jsonParser = Json

    @Test
    fun `ping-pong with older version should work`() {
        val expectedScore: Score = getExampleScore0()
        val input = expectedScore.toInput()

        postJsonRequest<InputScore, ResponseInputScore>(
            "http://localhost:8080/score",
            httpClient,
            jsonParser,
            input,
        ).flatMap { response -> response.inputScore.validateInput() }
            .shouldBeRight(expectedScore)
    }
}

@Serializable
data class ResponseInputScore(
    val inputScore: InputScore,
)
