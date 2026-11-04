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
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Files
import java.nio.file.Paths

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractCompatibilityTest<Error, A> {
    private val jsonParser: Json =
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            explicitNulls = false
        }

    /**
     * Resolves example paths against the repository root, set explicitly by the Gradle build,
     * rather than the JVM's current working directory.
     */
    private val rootProjectDir =
        Paths.get(
            System.getProperty("rootProjectDir")
                ?: error("System property 'rootProjectDir' is not set."),
        )

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

    fun exampleArguments(): List<Arguments> =
        examples().map { (jsonExamplePath, a) -> Arguments.of(jsonExamplePath, a) }

    /**
     * Compatibility means that JSON written by any SDK version reads into the same Kotlin object, and that this
     * object survives being written and read again. It does not mean that the JSON is written back identically:
     * the current SDK writes canonical JSON, and an older SDK drops fields it doesn't know. Writer stability is
     * tested in the main module, in `ExamplesTest`.
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("exampleArguments")
    fun `fromJson should read JSON into the expected object and the object should survive toJson and fromJson`(
        jsonExamplePath: String,
        a: A,
    ) {
        val path = rootProjectDir.resolve(jsonExamplePath)
        val json = Files.readString(path)

        fromJson(jsonParser, json) shouldBeRight a

        val written =
            toJson(jsonParser, a).getOrElse { error ->
                throw AssertionError("toJson failed for $jsonExamplePath, error is: $error")
            }
        fromJson(jsonParser, written) shouldBeRight a
    }
}
