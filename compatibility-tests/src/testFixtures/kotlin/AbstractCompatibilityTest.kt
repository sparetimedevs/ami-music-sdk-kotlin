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

package com.sparetimedevs.ami.compat

import arrow.core.Either
import arrow.core.getOrElse
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.json.shouldEqualJson
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.test.Test

abstract class AbstractCompatibilityTest<Error, A> {
    private val jsonParser: Json = Json.Default

    /**
     * Map of JSON example path -> expected Kotlin object
     */
    protected abstract fun examples(): Map<String, A>

    /**
     * Deserialize JSON -> Kotlin
     */
    protected abstract fun fromJson(
        jsonParser: Json,
        input: String,
    ): Either<Error, A>

    /**
     * Serialize Kotlin -> JSON
     */
    protected abstract fun toJson(
        jsonParser: Json,
        value: A,
    ): Either<Error, String>

    @Test
    fun `fromJson and toJson should work with examples in JSON and Kotlin code`() {
        examples().forEach { (jsonExamplePath, a) ->
            val path = Paths.get(jsonExamplePath)
            val json = Files.readString(path)

            fromJson(jsonParser, json) shouldBeRight a
            toJson(jsonParser, a).getOrElse {
                throw RuntimeException("Test failed")
            } shouldEqualJson json
        }
    }
}
