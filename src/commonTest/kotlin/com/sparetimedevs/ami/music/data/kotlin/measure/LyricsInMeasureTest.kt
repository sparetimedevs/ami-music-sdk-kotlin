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

class LyricsInMeasureTest {
    private val json = AmiMusicSdk.json
    private val fourFour = """"attributes":{"time":{"beats":4,"beatType":4}}"""

    private fun lyric(
        numerator: Int,
        denominator: Int,
        text: String = "la",
        verse: Int? = null,
    ) = """{"offset":{"numerator":$numerator,"denominator":$denominator},"text":"$text"${verse?.let {
        ""","verse":$it"""
    } ?: ""}}"""

    private fun scoreJson(vararg measures: String): String =
        """{"id":"s-1","parts":[{"id":"p-1","measures":[${
            measures.joinToString(",") { if (it.isEmpty()) """{"notes":[]}""" else """{$it,"notes":[]}""" }
        }]}]}"""

    private fun lyrics(vararg lyrics: String) = """"lyrics":[${lyrics.joinToString(",")}]"""

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
    fun `lyrics are read with their verse and a measure without lyrics has none`() {
        val measures =
            read(scoreJson("$fourFour,${lyrics(lyric(0, 1, "Hel"), lyric(1, 2, "lo", 2))}", "")).parts.single().measures

        measures[0].lyrics.map { Triple(it.offset, it.text, it.verse) } shouldBe
            listOf(Triple(Fraction.ZERO, "Hel", 1), Triple(Fraction.of(1, 2), "lo", 2))
        measures[1].lyrics shouldBe emptyList()
    }

    @Test
    fun `verse 1 is written as none and an explicit verse 1 reads the same as none`() {
        val explicit = read(scoreJson("$fourFour,${lyrics(lyric(0, 1, verse = 1))}"))

        explicit shouldBe read(scoreJson("$fourFour,${lyrics(lyric(0, 1))}"))
        write(explicit) shouldEqualJson scoreJson("$fourFour,${lyrics(lyric(0, 1))}")
    }

    @Test
    fun `an empty list of lyrics reads the same as none and is written as none`() {
        val withEmpty = read(scoreJson("$fourFour,\"lyrics\":[]"))

        withEmpty shouldBe read(scoreJson(fourFour))
        write(withEmpty) shouldEqualJson scoreJson(fourFour)
    }

    @Test
    fun `text is kept exactly as written`() {
        val input =
            scoreJson("$fourFour,${lyrics(lyric(0, 1, " leading"), lyric(1, 2, "trailing "), lyric(3, 4, " "))}")
        val score = read(input)

        score.parts
            .single()
            .measures
            .single()
            .lyrics
            .map { it.text } shouldBe listOf(" leading", "trailing ", " ")
        write(score) shouldEqualJson input
    }

    @Test
    fun `two verses may share offsets and round trip`() {
        val input =
            scoreJson(
                "$fourFour,${lyrics(
                    lyric(0, 1, "one"),
                    lyric(0, 1, "uno", 2),
                    lyric(1, 2, "two"),
                    lyric(1, 2, "dos", 2),
                )}",
            )

        write(read(input)) shouldEqualJson input
    }

    @Test
    fun `offsets must strictly increase within a verse`() {
        errorTexts(
            scoreJson("$fourFour,${lyrics(lyric(1, 2), lyric(0, 1, verse = 2), lyric(1, 2), lyric(1, 4, verse = 2))}"),
        ) shouldBe
            listOf(
                "score s-1, part p-1, measure 0, lyric 2: Offset: Offset 1/2 must be bigger than the previous offset 1/2",
            )
    }

    @Test
    fun `an offset outside the measure is located at its lyric`() {
        errorTexts(scoreJson(fourFour, lyrics(lyric(0, 1), lyric(1, 1)))) shouldBe
            listOf(
                "score s-1, part p-1, measure 1, lyric 1: Offset: Offset 1/1 is outside the measure (length 1)",
            )
    }

    @Test
    fun `an empty text is a validation error at its lyric`() {
        errorTexts(scoreJson("$fourFour,${lyrics(lyric(0, 1), lyric(1, 2, ""))}")).map {
            it.substringBefore(": ", "") +
                "|" +
                it.split(": ")[1]
        } shouldBe
            listOf("score s-1, part p-1, measure 0, lyric 1|LyricText")
    }

    @Test
    fun `a text of 512 characters is valid and 513 is not`() {
        read(scoreJson("$fourFour,${lyrics(lyric(0, 1, "a".repeat(512)))}"))
        errorTexts(scoreJson("$fourFour,${lyrics(lyric(0, 1, "a".repeat(513)))}")).map { it.split(": ")[1] } shouldBe
            listOf("LyricText")
    }

    @Test
    fun `a verse outside 1 to 99 is a validation error at its lyric`() {
        read(scoreJson("$fourFour,${lyrics(lyric(0, 1, verse = 99))}"))
        errorTexts(scoreJson("$fourFour,${lyrics(lyric(0, 1, verse = 0), lyric(1, 2, verse = 100))}")).map {
            it.split(": ").take(2).joinToString("|")
        } shouldBe
            listOf(
                "score s-1, part p-1, measure 0, lyric 0|LyricVerse",
                "score s-1, part p-1, measure 0, lyric 1|LyricVerse",
            )
    }

    @Test
    fun `an offset that is not a valid fraction is reported at its lyric`() {
        errorTexts(scoreJson("$fourFour,${lyrics(lyric(2, 4))}")).map { it.substringBefore(": ") } shouldBe
            listOf("score s-1, part p-1, measure 0, lyric 0")
    }
}
