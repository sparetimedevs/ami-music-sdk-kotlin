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

package com.sparetimedevs.ami.music.data.kotlin.measure

import arrow.core.getOrElse
import com.sparetimedevs.ami.AmiMusicSdk
import com.sparetimedevs.ami.core.AccumulatedValidationErrors
import com.sparetimedevs.ami.music.data.kotlin.chord.ChordKind
import com.sparetimedevs.ami.music.data.kotlin.fraction.Fraction
import com.sparetimedevs.ami.music.data.kotlin.score.Score
import com.sparetimedevs.ami.music.input.validation.toSerializable
import com.sparetimedevs.ami.music.serialization.fromJson
import com.sparetimedevs.ami.music.serialization.toJson
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class HarmonyInMeasureTest {
    private val json = AmiMusicSdk.json
    private val fourFour = """"attributes":{"time":{"beats":4,"beatType":4}}"""

    private fun harmony(
        numerator: Int,
        denominator: Int,
        root: String = "C",
        kind: String = "MAJOR",
        extra: String = "",
    ) =
        """{"offset":{"numerator":$numerator,"denominator":$denominator},"chord":{"kind":"$kind","root":"$root"$extra}}"""

    /** A one part score; each element is the body of a measure (without its notes), harmonies come last. */
    private fun scoreJson(vararg measures: String): String =
        """{"id":"s-1","parts":[{"id":"p-1","measures":[${
            measures.joinToString(",") { if (it.isEmpty()) """{"notes":[]}""" else """{$it,"notes":[]}""" }
        }]}]}"""

    private fun harmonies(vararg harmonies: String) = """"harmonies":[${harmonies.joinToString(",")}]"""

    private fun read(input: String): Score = fromJson(json, input).getOrElse { throw RuntimeException(it.message) }

    private fun errorTexts(input: String): List<String> =
        fromJson(json, input)
            .shouldBeLeft()
            .shouldBeInstanceOf<AccumulatedValidationErrors>()
            .toSerializable()
            .errors
            .map { "${it.locationAsText()}: ${it.property}: ${it.message}" }

    @Test
    fun `harmonies are read and a measure without harmonies has none`() {
        val measures =
            read(
                scoreJson("$fourFour,${harmonies(harmony(0, 1), harmony(1, 2, "D", "MINOR"))}", ""),
            ).parts.single().measures

        measures[0].harmonies.map { it.offset to it.chord.kind } shouldBe
            listOf(Fraction.ZERO to ChordKind.MAJOR, Fraction.of(1, 2) to ChordKind.MINOR)
        measures[1].harmonies shouldBe emptyList()
    }

    @Test
    fun `an empty list of harmonies reads the same as none and is written as none`() {
        val withEmpty = read(scoreJson("$fourFour,\"harmonies\":[]"))

        withEmpty shouldBe read(scoreJson(fourFour))
        toJson(json, withEmpty).getOrElse { throw RuntimeException(it.message) } shouldEqualJson scoreJson(fourFour)
    }

    @Test
    fun `a part with chords only measures round trips to identical json`() {
        val input =
            scoreJson(
                "$fourFour,${harmonies(harmony(0, 1), harmony(1, 2, "D", "MINOR"))}",
                harmonies(harmony(0, 1, "G", "DOMINANT", ""","bass":"B"""")),
            )

        toJson(json, read(input)).getOrElse { throw RuntimeException(it.message) } shouldEqualJson input
    }

    @Test
    fun `an offset outside the measure is located at its harmony`() {
        errorTexts(scoreJson(fourFour, "", harmonies(harmony(0, 1), harmony(5, 4)))) shouldBe
            listOf(
                "score s-1, part p-1, measure 2, harmony 1: Offset: " +
                    "Offset 5/4 is outside the measure (length 1)",
            )
    }

    @Test
    fun `an offset equal to the measure length is outside the measure`() {
        errorTexts(scoreJson("$fourFour,${harmonies(harmony(1, 1))}")).map { it.substringBefore(": O") } shouldBe
            listOf("score s-1, part p-1, measure 0, harmony 0")
    }

    @Test
    fun `offsets must strictly increase`() {
        errorTexts(scoreJson("$fourFour,${harmonies(harmony(1, 2), harmony(1, 2), harmony(1, 4))}")).map {
            it.substringBefore(": O")
        } shouldBe
            listOf("score s-1, part p-1, measure 0, harmony 1", "score s-1, part p-1, measure 0, harmony 2")
    }

    @Test
    fun `an offset is checked against the length of a pickup measure`() {
        val pickup = """"pickup":{"numerator":1,"denominator":4}"""
        val valid = scoreJson("$fourFour,$pickup,${harmonies(harmony(0, 1), harmony(1, 8))}")

        read(valid)
            .parts
            .single()
            .measures
            .single()
            .harmonies.size shouldBe 2
        errorTexts(scoreJson("$fourFour,$pickup,${harmonies(harmony(1, 4))}")) shouldBe
            listOf(
                "score s-1, part p-1, measure 0, harmony 0: Offset: Offset 1/4 is outside the measure (length 1/4)",
            )
    }

    @Test
    fun `an offset is checked against a carried forward time signature`() {
        val threeFour = """"attributes":{"time":{"beats":3,"beatType":4}}"""

        errorTexts(scoreJson(fourFour, "$threeFour,${harmonies(harmony(3, 4))}", harmonies(harmony(3, 4)))) shouldBe
            listOf(
                "score s-1, part p-1, measure 1, harmony 0: Offset: Offset 3/4 is outside the measure (length 3/4)",
                "score s-1, part p-1, measure 2, harmony 0: Offset: Offset 3/4 is outside the measure (length 3/4)",
            )
    }

    @Test
    fun `an offset that is not a valid fraction is reported at its harmony`() {
        errorTexts(scoreJson("$fourFour,${harmonies(harmony(2, 4))}")).map {
            it.substringBefore(": ", "").let { _ ->
                it.substringBefore(": ")
            }
        } shouldBe
            listOf("score s-1, part p-1, measure 0, harmony 0")
    }

    @Test
    fun `a chord degree error is located at its harmony`() {
        val badDegree = ""","degrees":[{"value":20,"alter":0,"type":"ADD"}]"""

        errorTexts(scoreJson("$fourFour,${harmonies(harmony(0, 1), harmony(1, 2, extra = badDegree))}")).map {
            it.substringBefore(": ")
        } shouldBe
            listOf("score s-1, part p-1, measure 0, harmony 1")
    }

    @Test
    fun `a chord error is serialized with the harmony kind`() {
        val errors =
            fromJson(json, scoreJson("$fourFour,${harmonies(harmony(0, 1, kind = "NOPE"))}"))
                .shouldBeLeft()
                .shouldBeInstanceOf<AccumulatedValidationErrors>()
                .toSerializable()

        json.encodeToString(
            com.sparetimedevs.ami.music.input.validation.SerializableValidationErrors
                .serializer(),
            errors,
        ) shouldEqualJson
            """{"message":"${errors.message}","errors":[{"message":"${errors.errors.single().message}","property":"ChordKind",
            "location":[{"kind":"score","id":"s-1"},{"kind":"part","id":"p-1"},{"kind":"measure","index":0},{"kind":"harmony","index":0}]}]}"""
    }

    @Test
    fun `C on beat 1 and D on beat 3 in chords only measures round trips to identical json`() {
        val input =
            scoreJson(
                "$fourFour,${harmonies(harmony(0, 1), harmony(1, 2, "D"))}",
                harmonies(harmony(0, 1, "A", "MINOR"), harmony(1, 2, "E", "MAJOR", ""","bass":"F_SHARP"""")),
            )
        val score = read(input)

        score.parts
            .single()
            .measures
            .map { it.notes } shouldBe listOf(emptyList(), emptyList())
        toJson(json, score).getOrElse { throw RuntimeException(it.message) } shouldEqualJson input
    }
}
