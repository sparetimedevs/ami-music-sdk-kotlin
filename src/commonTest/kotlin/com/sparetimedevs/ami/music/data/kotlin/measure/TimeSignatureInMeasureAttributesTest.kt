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
import com.sparetimedevs.ami.music.data.kotlin.score.Score
import com.sparetimedevs.ami.music.data.kotlin.timesignature.BeatType
import com.sparetimedevs.ami.music.data.kotlin.timesignature.Beats
import com.sparetimedevs.ami.music.data.kotlin.timesignature.TimeSignature
import com.sparetimedevs.ami.music.input.validation.toSerializable
import com.sparetimedevs.ami.music.serialization.fromJson
import com.sparetimedevs.ami.music.serialization.toJson
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class TimeSignatureInMeasureAttributesTest {
    private val json = AmiMusicSdk.json

    private fun timeSignature(
        beats: Int,
        beatType: Int,
    ) = TimeSignature(Beats.unsafeCreate(beats.toByte()), BeatType.unsafeCreate(beatType.toByte()))

    private fun time(
        beats: Int,
        beatType: Int,
    ) = """{"time":{"beats":$beats,"beatType":$beatType}}"""

    /** A one part score, with the given measure attributes (raw JSON, or null to leave them out). */
    private fun scoreJson(vararg attributes: String?): String =
        """{"id":"s-1","parts":[{"id":"p-1","measures":[${
            attributes.joinToString(",") { attribute ->
                if (attribute == null) """{"notes":[]}""" else """{"attributes":$attribute,"notes":[]}"""
            }
        }]}]}"""

    private fun read(input: String): Score = fromJson(json, input).getOrElse { throw RuntimeException(it.message) }

    private fun written(score: Score): String = toJson(json, score).getOrElse { throw RuntimeException(it.message) }

    private fun errorTexts(input: String): List<String> =
        fromJson(json, input)
            .shouldBeLeft()
            .shouldBeInstanceOf<AccumulatedValidationErrors>()
            .toSerializable()
            .errors
            .map { "${it.locationAsText()}: ${it.property}" }

    @Test
    fun `beats of zero is a validation error located at its measure`() {
        errorTexts(scoreJson(time(4, 4), time(0, 4))) shouldBe listOf("score s-1, part p-1, measure 1: Beats")
    }

    @Test
    fun `negative beat type is a validation error located at its measure`() {
        errorTexts(scoreJson(time(4, -4))) shouldBe listOf("score s-1, part p-1, measure 0: BeatType")
    }

    @Test
    fun `beats outside the byte range is a validation error and not a wrapped value`() {
        // 256 would wrap to 0 and 129 to a negative byte.
        errorTexts(scoreJson(time(256, 4), time(4, 129))) shouldBe
            listOf("score s-1, part p-1, measure 0: Beats", "score s-1, part p-1, measure 1: BeatType")
    }

    @Test
    fun `empty attributes read the same as no attributes`() {
        read(scoreJson("{}", "{}")) shouldBe read(scoreJson(null, null))
    }

    @Test
    fun `a first measure without time signature gets 4 over 4`() {
        val measures = read(scoreJson(null, null)).parts.single().measures

        measures[0].attributes shouldBe null
        measures[0].timeSignature shouldBe timeSignature(4, 4)
        measures[1].attributes shouldBe null
        measures[1].timeSignature shouldBe timeSignature(4, 4)
    }

    @Test
    fun `a time signature carries forward until the next one`() {
        val measures =
            read(scoreJson(null, null, time(3, 4), null, null)).parts.single().measures

        measures.map { it.timeSignature } shouldBe
            listOf(
                timeSignature(4, 4),
                timeSignature(4, 4),
                timeSignature(3, 4),
                timeSignature(3, 4),
                timeSignature(3, 4),
            )
    }

    @Test
    fun `measure keeps the time signature it is given`() {
        val threeFour = timeSignature(3, 4)

        Measure(null, emptyList(), threeFour).timeSignature shouldBe threeFour
        Measure(null, emptyList(), threeFour).attributes shouldBe null
        Measure(MeasureAttributes(), emptyList(), threeFour).attributes shouldBe null
    }

    @Test
    fun `a time signature that repeats the previous one is not written`() {
        written(read(scoreJson(time(3, 4), time(3, 4), null, time(4, 4), time(4, 4)))) shouldEqualJson
            scoreJson(time(3, 4), null, null, time(4, 4), null)
    }

    @Test
    fun `a time signature that repeats the previous one reads the same as leaving it out`() {
        read(scoreJson(time(3, 4), time(3, 4))) shouldBe read(scoreJson(time(3, 4), null))
    }

    @Test
    fun `json from 0 0 1 snapshot and its canonical form read into equal scores`() {
        val older = scoreJson("{}", "{}", "{}")
        val canonical = scoreJson(time(4, 4), null, null)

        read(older) shouldBe read(canonical)
    }

    @Test
    fun `writing a score gives the canonical json`() {
        val canonical = scoreJson(time(4, 4), null, null)

        written(read(scoreJson("{}", "{}", "{}"))) shouldEqualJson canonical
        written(read(scoreJson(null, null, null))) shouldEqualJson canonical
    }

    @Test
    fun `reading and then writing canonical json gives identical json`() {
        val canonical = scoreJson(time(3, 4), null, time(4, 4), null, time(6, 8))

        written(read(canonical)) shouldEqualJson canonical
    }
}
