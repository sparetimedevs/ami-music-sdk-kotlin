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

import arrow.core.Either
import arrow.core.flatMap
import com.sparetimedevs.ami.core.DomainError
import com.sparetimedevs.ami.core.ParseError
import com.sparetimedevs.ami.music.data.kotlin.score.Score
import com.sparetimedevs.ami.music.input.toInput
import com.sparetimedevs.ami.music.input.validation.validateInput
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

public fun fromJson(jsonParser: Json, input: String): Either<DomainError, Score> =
    Either.catch { jsonParser.decodeFromString<com.sparetimedevs.ami.music.input.Score>(input) }
        .mapLeft {
            ParseError(
                "There was an exception while parsing the input. The exception is: ${it.message}"
            )
        }
        .flatMap { it.validateInput() }

public fun toJson(jsonParser: Json, score: Score): Either<DomainError, String> =
    Either.catch {
            val scoreInputType: com.sparetimedevs.ami.music.input.Score = score.toInput()
            jsonParser.encodeToString(scoreInputType)
        }
        .mapLeft {
            ParseError(
                "There was an exception while encoding to JSON. The exception is: ${it.message}"
            )
        }
