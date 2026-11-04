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

package com.sparetimedevs.ami.music.input.validation

import com.sparetimedevs.ami.AmiMusicSdk
import com.sparetimedevs.ami.core.AccumulatedValidationErrors
import com.sparetimedevs.ami.music.serialization.fromJson
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class AccumulatedErrorsTest {
    @Test
    fun `errors from several new rules accumulate in one result and each has its location`() {
        val input =
            """
            {"id":"s-1","parts":[{"id":"p-1","measures":[
              {"attributes":{"time":{"beats":4,"beatType":4},"tempo":{"quarterNotesPerMinute":500}},"notes":[]},
              {"notes":[],"harmonies":[
                {"offset":{"numerator":2,"denominator":4},"chord":{"root":"C","kind":"MAJOR"}}]},
              {"notes":[],"harmonies":[
                {"offset":{"numerator":0,"denominator":1},"chord":{"root":"C","kind":"MAJOR"}},
                {"offset":{"numerator":1,"denominator":2},"chord":{"root":"D","kind":"NOT_A_KIND"}}]},
              {"notes":[],"lyrics":[{"offset":{"numerator":0,"denominator":1},"text":""}]}
            ]}]}
            """.trimIndent()

        val errors =
            fromJson(AmiMusicSdk.json, input)
                .shouldBeLeft()
                .shouldBeInstanceOf<AccumulatedValidationErrors>()
                .toSerializable()
                .errors
                .map { it.locationAsText() to it.property }

        errors shouldContainExactlyInAnyOrder
            listOf(
                "score s-1, part p-1, measure 0" to "QuarterNotesPerMinute",
                "score s-1, part p-1, measure 1, harmony 0" to "Fraction",
                "score s-1, part p-1, measure 2, harmony 1" to "ChordKind",
                "score s-1, part p-1, measure 3, lyric 0" to "LyricText",
            )
    }
}
