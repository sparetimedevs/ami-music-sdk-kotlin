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
import com.sparetimedevs.ami.music.data.kotlin.key.Fifths
import com.sparetimedevs.ami.music.data.kotlin.key.Key
import com.sparetimedevs.ami.music.data.kotlin.key.KeyMode
import com.sparetimedevs.ami.music.data.kotlin.score.Score
import com.sparetimedevs.ami.music.data.kotlin.tempo.QuarterNotesPerMinute
import com.sparetimedevs.ami.music.data.kotlin.tempo.Tempo
import com.sparetimedevs.ami.music.input.validation.toSerializable
import com.sparetimedevs.ami.music.serialization.fromJson
import com.sparetimedevs.ami.music.serialization.toJson
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class KeyAndTempoInMeasureAttributesTest {
    private val json = AmiMusicSdk.json
    private val time = """"time":{"beats":4,"beatType":4}"""

    private fun key(
        fifths: Int,
        mode: String,
    ) = """"key":{"fifths":$fifths,"mode":"$mode"}"""

    private fun tempo(bpm: Int) = """"tempo":{"quarterNotesPerMinute":$bpm}"""

    /** A one part score; each element is the body of a measure's attributes, or null to leave them out. */
    private fun scoreJson(vararg attributes: String?): String =
        """{"id":"s-1","parts":[{"id":"p-1","measures":[${
            attributes.joinToString(",") { attribute ->
                if (attribute == null) """{"notes":[]}""" else """{"attributes":{$attribute},"notes":[]}"""
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
    fun `fifths outside minus 7 to 7 is a validation error located at its measure`() {
        errorTexts(scoreJson(time, key(8, "MAJOR"), key(-8, "MINOR"))) shouldBe
            listOf("score s-1, part p-1, measure 1: Fifths", "score s-1, part p-1, measure 2: Fifths")
    }

    @Test
    fun `fifths at the limits are valid`() {
        read(scoreJson("$time,${key(-7, "MINOR")}", key(7, "MAJOR"))).parts.single().keyAt(1) shouldBe
            Key(Fifths.unsafeCreate(7), KeyMode.MAJOR)
    }

    @Test
    fun `unknown key mode is a validation error located at its measure`() {
        errorTexts(scoreJson(time, key(0, "DORIAN"))) shouldBe listOf("score s-1, part p-1, measure 1: KeyMode")
    }

    @Test
    fun `fifths and mode errors accumulate`() {
        errorTexts(scoreJson("$time,${key(9, "dorian")}")) shouldBe
            listOf("score s-1, part p-1, measure 0: Fifths", "score s-1, part p-1, measure 0: KeyMode")
    }

    @Test
    fun `tempo outside 20 to 400 is a validation error located at its measure`() {
        errorTexts(scoreJson(time, tempo(19), tempo(401))) shouldBe
            listOf(
                "score s-1, part p-1, measure 1: QuarterNotesPerMinute",
                "score s-1, part p-1, measure 2: QuarterNotesPerMinute",
            )
    }

    @Test
    fun `tempo at the limits is valid`() {
        val part = read(scoreJson("$time,${tempo(20)}", tempo(400))).parts.single()

        part.tempoAt(0) shouldBe Tempo(QuarterNotesPerMinute.unsafeCreate(20))
        part.tempoAt(1) shouldBe Tempo(QuarterNotesPerMinute.unsafeCreate(400))
    }

    @Test
    fun `key and tempo are only written on the measures that state them`() {
        val canonical = scoreJson("$time,${key(2, "MAJOR")},${tempo(120)}", null, tempo(90))

        written(read(canonical)) shouldEqualJson canonical
    }

    @Test
    fun `key and tempo carry forward into the attributes of later measures`() {
        val measures =
            read(
                scoreJson("$time,${key(2, "MAJOR")},${tempo(120)}", null, tempo(90), null),
            ).parts.single().measures

        measures.map {
            it.attributes.key
                ?.fifths
                ?.value
                ?.toInt()
        } shouldBe listOf(2, 2, 2, 2)
        measures.map {
            it.attributes.tempo
                ?.quarterNotesPerMinute
                ?.value
                ?.toInt()
        } shouldBe listOf(120, 120, 90, 90)
    }

    @Test
    fun `a key or tempo that repeats the previous one is not written`() {
        written(
            read(scoreJson("$time,${key(2, "MAJOR")},${tempo(120)}", "${key(2, "MAJOR")},${tempo(120)}", tempo(120))),
        ) shouldEqualJson
            scoreJson("$time,${key(2, "MAJOR")},${tempo(120)}", null, null)
    }

    @Test
    fun `keyAt and tempoAt give the last stated value or null`() {
        val part =
            read(scoreJson(time, null, key(1, "MAJOR"), tempo(100), key(-3, "MINOR"), null, tempo(60))).parts.single()

        val keys = (0..6).map { part.keyAt(it) }
        keys shouldBe
            listOf(
                null,
                null,
                Key(Fifths.unsafeCreate(1), KeyMode.MAJOR),
                Key(Fifths.unsafeCreate(1), KeyMode.MAJOR),
                Key(Fifths.unsafeCreate(-3), KeyMode.MINOR),
                Key(Fifths.unsafeCreate(-3), KeyMode.MINOR),
                Key(Fifths.unsafeCreate(-3), KeyMode.MINOR),
            )
        val bpm =
            (0..6).map {
                part
                    .tempoAt(it)
                    ?.quarterNotesPerMinute
                    ?.value
                    ?.toInt()
            }
        bpm shouldBe listOf(null, null, null, 100, 100, 100, 60)
    }

    @Test
    fun `a part with a key change and a tempo change mid piece round trips to identical json`() {
        val canonical =
            scoreJson(
                "$time,${key(0, "MAJOR")},${tempo(100)}",
                null,
                "${key(-3, "MINOR")},${tempo(140)}",
                null,
            )

        written(read(canonical)) shouldEqualJson canonical
    }
}
