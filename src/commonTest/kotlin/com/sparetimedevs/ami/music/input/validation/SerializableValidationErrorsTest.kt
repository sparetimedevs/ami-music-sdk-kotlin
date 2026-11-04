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
import com.sparetimedevs.ami.core.validation.NoValidationIdentifier
import com.sparetimedevs.ami.core.validation.ValidationLocationKind
import com.sparetimedevs.ami.music.data.kotlin.part.PartId
import com.sparetimedevs.ami.music.data.kotlin.score.ScoreId
import com.sparetimedevs.ami.music.example.getExampleScore0InvalidInput
import com.sparetimedevs.ami.music.serialization.fromJson
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class SerializableValidationErrorsTest {
    private val scoreId = ScoreId.unsafeCreate("d737b4ae-fbaa-4b0d-9d36-d3651e30e93a")
    private val partId = PartId.unsafeCreate("p-1")

    private fun noteIdentifier() =
        ValidationIdentifierForNote(
            2,
            ValidationIdentifierForMeasure(
                3,
                ValidationIdentifierForPart(partId, ValidationIdentifierForScore(scoreId, NoValidationIdentifier)),
            ),
        )

    @Test
    fun `identifiers name the kind of their location`() {
        val note = noteIdentifier()
        val measure = note.validationIdentifierParent
        val part = measure.validationIdentifierParent
        val score = part.validationIdentifierParent

        listOf(note, measure, part, score).map { it.kind } shouldBe
            listOf(
                ValidationLocationKind.NOTE,
                ValidationLocationKind.MEASURE,
                ValidationLocationKind.PART,
                ValidationLocationKind.SCORE,
            )
        ValidationIdentifierForPitchOfChord(1, note).kind shouldBe ValidationLocationKind.PITCH
        NoValidationIdentifier.kind shouldBe null
    }

    @Test
    fun `note level location is a list of segments and renders as text`() {
        val location = noteIdentifier().locationSegments()

        location shouldBe
            listOf(
                ValidationLocationSegment(ValidationLocationKind.SCORE, id = scoreId.value),
                ValidationLocationSegment(ValidationLocationKind.PART, id = "p-1"),
                ValidationLocationSegment(ValidationLocationKind.MEASURE, index = 3),
                ValidationLocationSegment(ValidationLocationKind.NOTE, index = 2),
            )
        noteIdentifier().locationAsText() shouldBe "score ${scoreId.value}, part p-1, measure 3, note 2"
    }

    @Test
    fun `no validation identifier has an empty location`() {
        NoValidationIdentifier.locationSegments() shouldBe emptyList()
        NoValidationIdentifier.locationAsText() shouldBe ""
    }

    @Test
    fun `part level error from fromJson serializes with its location`() {
        val input = getExampleScore0InvalidInput()
        val json =
            AmiMusicSdk.json.encodeToString(
                input.copy(parts = input.parts.map { it.copy(name = "x".repeat(513)) }),
            )

        val error = fromJson(AmiMusicSdk.json, json).shouldBeLeft().shouldBeInstanceOf<AccumulatedValidationErrors>()
        val partError = error.toSerializable().errors.first { it.property == "PartName" }

        AmiMusicSdk.json.encodeToString(partError) shouldBe
            """{"message":"Part name can't be longer than 512 characters, the input was ${"x".repeat(513)}",""" +
            """"property":"PartName","location":[{"kind":"score","id":"d737b4ae-fbaa-4b0d-9d36-d3651e30e93a"},""" +
            """{"kind":"part","id":"p-1"}]}"""
        partError.locationAsText() shouldBe "score d737b4ae-fbaa-4b0d-9d36-d3651e30e93a, part p-1"
    }

    @Test
    fun `note level errors from fromJson serialize with their location`() {
        val json = AmiMusicSdk.json.encodeToString(getExampleScore0InvalidInput())

        val error = fromJson(AmiMusicSdk.json, json).shouldBeLeft().shouldBeInstanceOf<AccumulatedValidationErrors>()
        val errors = error.toSerializable().errors

        errors.map { it.property } shouldBe listOf("NoteDuration", "Octave", "NoteName")
        AmiMusicSdk.json.encodeToString(errors[1]) shouldBe
            """{"message":"Octave can't be greater than 12, the input was 127","property":"Octave",""" +
            """"location":[{"kind":"score","id":"d737b4ae-fbaa-4b0d-9d36-d3651e30e93a"},""" +
            """{"kind":"part","id":"p-1"},{"kind":"measure","index":0},{"kind":"note","index":1}]}"""
    }
}
