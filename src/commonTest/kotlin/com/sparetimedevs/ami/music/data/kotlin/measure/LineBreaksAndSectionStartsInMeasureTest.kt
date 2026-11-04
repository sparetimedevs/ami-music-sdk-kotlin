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

class LineBreaksAndSectionStartsInMeasureTest {
    private val json = AmiMusicSdk.json
    private val fourFour = """"attributes":{"time":{"beats":4,"beatType":4}}"""

    private fun fraction(
        numerator: Int,
        denominator: Int,
    ) = """{"numerator":$numerator,"denominator":$denominator}"""

    private fun section(
        numerator: Int,
        denominator: Int,
        label: String? = null,
    ) = """{"offset":${fraction(numerator, denominator)}${label?.let { ""","label":"$it"""" } ?: ""}}"""

    private fun lineBreaks(vararg lineBreaks: String) = """"lineBreaks":[${lineBreaks.joinToString(",")}]"""

    private fun sections(vararg sections: String) = """"sectionStarts":[${sections.joinToString(",")}]"""

    /** The first measure states 4/4, all following measures are given as they are. */
    private fun scoreJson(
        first: String,
        vararg more: String,
    ): String {
        val measures = listOf("$fourFour,$first") + more
        return """{"id":"s-1","parts":[{"id":"p-1","measures":[${measures.joinToString(
            ",",
        ) { "{$it,\"notes\":[]}" }}]}]}"""
    }

    private fun read(input: String): Score = fromJson(json, input).getOrElse { throw RuntimeException(it.message) }

    private fun write(score: Score): String = toJson(json, score).getOrElse { throw RuntimeException(it.message) }

    private fun errorTexts(input: String): List<String> =
        fromJson(json, input)
            .shouldBeLeft()
            .shouldBeInstanceOf<AccumulatedValidationErrors>()
            .toSerializable()
            .errors
            .map { "${it.locationAsText()}: ${it.property}: ${it.message}" }

    private val noTracks = """"harmonies":[]"""

    @Test
    fun `line breaks and section starts are read and a measure without them has none`() {
        val measures =
            read(
                scoreJson(
                    "${lineBreaks(fraction(1, 2))},${sections(section(1, 4, "Refrein"), section(3, 4))}",
                    noTracks,
                ),
            ).parts
                .single()
                .measures

        measures[0].lineBreaks shouldBe listOf(Fraction.of(1, 2))
        measures[0].sectionStarts.map { it.offset to it.label } shouldBe
            listOf(Fraction.of(1, 4) to "Refrein", Fraction.of(3, 4) to null)
        measures[1].lineBreaks shouldBe emptyList()
        measures[1].sectionStarts shouldBe emptyList()
    }

    @Test
    fun `empty lists read the same as none and are written as none`() {
        val withEmpty = read(scoreJson("\"lineBreaks\":[],\"sectionStarts\":[]"))

        withEmpty shouldBe read(scoreJson(noTracks))
        write(withEmpty) shouldEqualJson scoreJson(noTracks).replace(",$noTracks", "")
    }

    @Test
    fun `line breaks and section starts round trip to identical JSON`() {
        val input =
            scoreJson(
                noTracks,
                "${lineBreaks(fraction(0, 1), fraction(1, 2))},${sections(section(0, 1, "Couplet"), section(1, 2))}",
            ).replace(",$noTracks", "")

        write(read(input)) shouldEqualJson input
    }

    @Test
    fun `a line break past the end of the measure is located at its line break`() {
        errorTexts(scoreJson(noTracks, lineBreaks(fraction(1, 4), fraction(5, 4)))) shouldBe
            listOf("score s-1, part p-1, measure 1, line break 1: Offset: Offset 5/4 is outside the measure (length 1)")
    }

    @Test
    fun `a line break as long as the measure is outside it`() {
        errorTexts(scoreJson(noTracks, lineBreaks(fraction(1, 1)))).map { it.substringBefore(": ") } shouldBe
            listOf("score s-1, part p-1, measure 1, line break 0")
    }

    @Test
    fun `line breaks must strictly increase`() {
        errorTexts(scoreJson(noTracks, lineBreaks(fraction(1, 2), fraction(1, 2), fraction(1, 4)))) shouldBe
            listOf(
                "score s-1, part p-1, measure 1, line break 1: Offset: Offset 1/2 must be bigger than the previous offset 1/2",
                "score s-1, part p-1, measure 1, line break 2: Offset: Offset 1/4 must be bigger than the previous offset 1/2",
            )
    }

    @Test
    fun `a line break offset that is not a valid fraction is reported at its line break`() {
        errorTexts(scoreJson(noTracks, lineBreaks(fraction(2, 4)))).map { it.substringBefore(": ") } shouldBe
            listOf("score s-1, part p-1, measure 1, line break 0")
    }

    @Test
    fun `a section start past the end of the measure is located at its section start`() {
        errorTexts(scoreJson(noTracks, sections(section(3, 2, "A")))) shouldBe
            listOf(
                "score s-1, part p-1, measure 1, section start 0: Offset: Offset 3/2 is outside the measure (length 1)",
            )
    }

    @Test
    fun `section starts must strictly increase`() {
        errorTexts(scoreJson(noTracks, sections(section(1, 2, "A"), section(1, 4, "B")))) shouldBe
            listOf(
                "score s-1, part p-1, measure 1, section start 1: Offset: Offset 1/4 must be bigger than the previous offset 1/2",
            )
    }

    @Test
    fun `a label of 64 characters is valid and 65 is not`() {
        read(scoreJson(noTracks, sections(section(0, 1, "a".repeat(64)))))
        errorTexts(scoreJson(noTracks, sections(section(0, 1, "a".repeat(65))))).map {
            it.split(": ").take(2).joinToString("|")
        } shouldBe listOf("score s-1, part p-1, measure 1, section start 0|SectionStartLabel")
    }

    @Test
    fun `a label that is empty after trimming is a validation error`() {
        errorTexts(scoreJson(noTracks, sections(section(0, 1, ""), section(1, 2, "  ")))).map {
            it.split(": ").take(2).joinToString("|")
        } shouldBe
            listOf(
                "score s-1, part p-1, measure 1, section start 0|SectionStartLabel",
                "score s-1, part p-1, measure 1, section start 1|SectionStartLabel",
            )
    }

    @Test
    fun `a line break at offset 0 of the first measure is a validation error`() {
        errorTexts(scoreJson(lineBreaks(fraction(0, 1), fraction(1, 2)))).map {
            it.split(": ").take(2).joinToString("|")
        } shouldBe listOf("score s-1, part p-1, measure 0, line break 0|Offset")
    }

    @Test
    fun `a section start at offset 0 of the first measure needs a label`() {
        errorTexts(scoreJson(sections(section(0, 1)))).map { it.split(": ").take(2).joinToString("|") } shouldBe
            listOf("score s-1, part p-1, measure 0, section start 0|SectionStartLabel")
        read(scoreJson(sections(section(0, 1, "Couplet"))))
    }

    @Test
    fun `the song start rules only apply to the first measure of a part`() {
        read(scoreJson(noTracks, "${lineBreaks(fraction(0, 1))},${sections(section(0, 1))}"))
    }

    @Test
    fun `a line break or section start in the middle of the first measure is valid without a label`() {
        read(scoreJson("${lineBreaks(fraction(1, 4))},${sections(section(1, 2))}"))
    }

    @Test
    fun `the measure from the spec round trips to identical JSON`() {
        val measure =
            """
            {
              "notes": [],
              "harmonies": [
                { "offset": { "numerator": 0, "denominator": 1 }, "chord": { "root": "C", "kind": "MAJOR" } },
                { "offset": { "numerator": 1, "denominator": 2 }, "chord": { "root": "D", "kind": "MAJOR" } }
              ],
              "lyrics": [
                { "offset": { "numerator": 0, "denominator": 1 }, "text": "This is a song, " },
                { "offset": { "numerator": 1, "denominator": 2 }, "text": "with some words" }
              ],
              "lineBreaks": [ { "numerator": 0, "denominator": 1 } ]
            }
            """.trimIndent()
        val input =
            """{"id":"s-1","parts":[{"id":"p-1","measures":[{"attributes":{"time":{"beats":4,"beatType":4}},"notes":[]},$measure]}]}"""

        write(read(input)) shouldEqualJson input
    }
}
