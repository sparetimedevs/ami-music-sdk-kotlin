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

class PickupInMeasureTest {
    private val json = AmiMusicSdk.json
    private val fourFour = """"attributes":{"time":{"beats":4,"beatType":4}}"""

    private fun pickup(
        numerator: Int,
        denominator: Int,
    ) = """"pickup":{"numerator":$numerator,"denominator":$denominator}"""

    /** A one part score; each element is the body of a measure (without its notes). */
    private fun scoreJson(vararg measures: String): String =
        """{"id":"s-1","parts":[{"id":"p-1","measures":[${
            measures.joinToString(",") { if (it.isEmpty()) """{"notes":[]}""" else """{$it,"notes":[]}""" }
        }]}]}"""

    private fun read(input: String): Score = fromJson(json, input).getOrElse { throw RuntimeException(it.message) }

    private fun errorTexts(input: String): List<String> =
        fromJson(json, input)
            .shouldBeLeft()
            .shouldBeInstanceOf<AccumulatedValidationErrors>()
            .toSerializable()
            .errors
            .map { "${it.locationAsText()}: ${it.property}" }

    @Test
    fun `a pickup is read and the length of its measure is the pickup`() {
        val measures = read(scoreJson("$fourFour,${pickup(1, 4)}", "")).parts.single().measures

        measures[0].pickup shouldBe Fraction.of(1, 4)
        measures[0].length shouldBe Fraction.of(1, 4)
        measures[1].pickup shouldBe null
        measures[1].length shouldBe Fraction.ONE
    }

    @Test
    fun `the length without a pickup follows the time signature`() {
        val threeFour = """"attributes":{"time":{"beats":3,"beatType":4}}"""

        read(scoreJson(threeFour))
            .parts
            .single()
            .measures
            .single()
            .length shouldBe Fraction.of(3, 4)
    }

    @Test
    fun `a pickup is written only when set`() {
        val input = scoreJson("$fourFour,${pickup(1, 4)}", "")

        toJson(json, read(input)).getOrElse { throw RuntimeException(it.message) } shouldEqualJson input
    }

    @Test
    fun `a pickup on the first measure of each part is valid`() {
        val input =
            """{"id":"s-1","parts":[
                {"id":"p-1","measures":[{$fourFour,${pickup(3, 8)},"notes":[]}]},
                {"id":"p-2","measures":[{$fourFour,${pickup(1, 4)},"notes":[]}]}]}"""

        read(input).parts.map { it.measures.single().pickup } shouldBe listOf(Fraction.of(3, 8), Fraction.of(1, 4))
    }

    @Test
    fun `a pickup on measure 2 is a validation error located at that measure`() {
        errorTexts(scoreJson(fourFour, pickup(1, 4))) shouldBe listOf("score s-1, part p-1, measure 1: Pickup")
    }

    @Test
    fun `a pickup as long as the whole measure is a validation error`() {
        errorTexts(scoreJson("$fourFour,${pickup(1, 1)}")) shouldBe listOf("score s-1, part p-1, measure 0: Pickup")
    }

    @Test
    fun `a pickup longer than the measure is a validation error`() {
        errorTexts(scoreJson("$fourFour,${pickup(5, 4)}")) shouldBe listOf("score s-1, part p-1, measure 0: Pickup")
    }

    @Test
    fun `a pickup of zero is a validation error`() {
        errorTexts(scoreJson("$fourFour,${pickup(0, 1)}")) shouldBe listOf("score s-1, part p-1, measure 0: Pickup")
    }

    @Test
    fun `a pickup is compared to the time signature of its own measure`() {
        val sixEight = """"attributes":{"time":{"beats":6,"beatType":8}}"""

        read(scoreJson("$sixEight,${pickup(1, 2)}"))
            .parts
            .single()
            .measures
            .single()
            .length shouldBe Fraction.of(1, 2)
        errorTexts(scoreJson("$sixEight,${pickup(3, 4)}")) shouldBe listOf("score s-1, part p-1, measure 0: Pickup")
    }

    @Test
    fun `a pickup that is not a valid fraction is reported for its fraction`() {
        errorTexts(scoreJson("$fourFour,${pickup(2, 8)}", pickup(1, 0))) shouldBe
            listOf("score s-1, part p-1, measure 0: Fraction", "score s-1, part p-1, measure 1: Denominator")
    }

    @Test
    fun `a score whose measures all have no notes validates and every measure is complete`() {
        val measures = read(scoreJson("$fourFour,${pickup(1, 4)}", "", "")).parts.single().measures

        measures.forEach { it.shouldBeInstanceOf<Measure.CompleteMeasure>() }
        measures.map { it.length } shouldBe listOf(Fraction.of(1, 4), Fraction.ONE, Fraction.ONE)
    }
}
