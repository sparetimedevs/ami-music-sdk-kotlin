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

class TextDirectionsInMeasureTest {
    private val json = AmiMusicSdk.json
    private val fourFour = """"attributes":{"time":{"beats":4,"beatType":4}}"""

    private fun direction(
        numerator: Int,
        denominator: Int,
        text: String = "2x",
    ) = """{"offset":{"numerator":$numerator,"denominator":$denominator},"text":"$text"}"""

    private fun scoreJson(vararg measures: String): String =
        """{"id":"s-1","parts":[{"id":"p-1","measures":[${
            measures.joinToString(",") { if (it.isEmpty()) """{"notes":[]}""" else """{$it,"notes":[]}""" }
        }]}]}"""

    private fun directions(vararg directions: String) = """"textDirections":[${directions.joinToString(",")}]"""

    private fun read(input: String): Score = fromJson(json, input).getOrElse { throw RuntimeException(it.message) }

    private fun write(score: Score): String = toJson(json, score).getOrElse { throw RuntimeException(it.message) }

    private fun errorTexts(input: String): List<String> =
        fromJson(json, input)
            .shouldBeLeft()
            .shouldBeInstanceOf<AccumulatedValidationErrors>()
            .toSerializable()
            .errors
            .map { "${it.locationAsText()}: ${it.property}: ${it.message}" }

    @Test
    fun `text directions are read and a measure without them has none`() {
        val measures =
            read(scoreJson("$fourFour,${directions(direction(0, 1, "Coda"), direction(1, 2, "rit."))}", ""))
                .parts
                .single()
                .measures

        measures[0].textDirections.map { it.offset to it.text } shouldBe
            listOf(Fraction.ZERO to "Coda", Fraction.of(1, 2) to "rit.")
        measures[1].textDirections shouldBe emptyList()
    }

    @Test
    fun `an empty list reads the same as none and is written as none`() {
        val withEmpty = read(scoreJson("$fourFour,\"textDirections\":[]"))

        withEmpty shouldBe read(scoreJson(fourFour))
        write(withEmpty) shouldEqualJson scoreJson(fourFour)
    }

    @Test
    fun `text directions round trip to identical JSON`() {
        val input = scoreJson("$fourFour,${directions(direction(0, 1, "Fine"), direction(1, 1, "2x"))}")

        write(read(input)) shouldEqualJson input
    }

    @Test
    fun `2x at the very end of the last measure is valid`() {
        read(scoreJson(fourFour, directions(direction(1, 1))))
    }

    @Test
    fun `an offset past the end of the measure is located at its text direction`() {
        errorTexts(scoreJson(fourFour, directions(direction(0, 1), direction(5, 4)))) shouldBe
            listOf(
                "score s-1, part p-1, measure 1, text direction 1: Offset: Offset 5/4 is outside the measure (length 1)",
            )
    }

    @Test
    fun `the end of a pickup measure is its length`() {
        val pickup = """"pickup":{"numerator":1,"denominator":4}"""
        read(scoreJson("$fourFour,$pickup,${directions(direction(1, 4))}"))
        errorTexts(
            scoreJson("$fourFour,$pickup,${directions(direction(1, 2))}"),
        ).map { it.split(": ").take(2) } shouldBe
            listOf(listOf("score s-1, part p-1, measure 0, text direction 0", "Offset"))
    }

    @Test
    fun `offsets may repeat and keep their order but may not decrease`() {
        val same = scoreJson("$fourFour,${directions(direction(1, 2, "a"), direction(1, 2, "b"))}")
        read(same)
            .parts
            .single()
            .measures
            .single()
            .textDirections
            .map { it.text } shouldBe listOf("a", "b")
        write(read(same)) shouldEqualJson same

        errorTexts(scoreJson("$fourFour,${directions(direction(1, 2), direction(1, 4), direction(3, 4))}")) shouldBe
            listOf(
                "score s-1, part p-1, measure 0, text direction 1: Offset: Offset 1/4 must be at least the previous offset 1/2",
            )
    }

    @Test
    fun `an empty text is a validation error at its text direction`() {
        errorTexts(scoreJson("$fourFour,${directions(direction(0, 1), direction(1, 2, ""))}")).map {
            it.split(": ").take(2).joinToString("|")
        } shouldBe listOf("score s-1, part p-1, measure 0, text direction 1|TextDirectionText")
    }

    @Test
    fun `a text of 512 characters is valid and 513 is not`() {
        read(scoreJson("$fourFour,${directions(direction(0, 1, "a".repeat(512)))}"))
        errorTexts(
            scoreJson("$fourFour,${directions(direction(0, 1, "a".repeat(513)))}"),
        ).map { it.split(": ")[1] } shouldBe
            listOf("TextDirectionText")
    }

    @Test
    fun `an offset that is not a valid fraction is reported at its text direction`() {
        errorTexts(scoreJson("$fourFour,${directions(direction(2, 4))}")).map { it.substringBefore(": ") } shouldBe
            listOf("score s-1, part p-1, measure 0, text direction 0")
    }
}
