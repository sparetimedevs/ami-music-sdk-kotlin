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

package com.sparetimedevs.ami.music.data.kotlin.chord

import arrow.core.getOrElse
import com.sparetimedevs.ami.AmiMusicSdk
import com.sparetimedevs.ami.music.data.kotlin.note.NoteName
import com.sparetimedevs.ami.music.input.validation.validate
import com.sparetimedevs.ami.music.serialization.toSerializable
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import com.sparetimedevs.ami.music.serialization.ChordDegree as SerializableChordDegree
import com.sparetimedevs.ami.music.serialization.ChordSymbol as SerializableChordSymbol

class ChordSymbolTest {
    private fun symbol(
        kind: String,
        root: String? = "C",
        kindText: String? = null,
        degrees: List<SerializableChordDegree>? = null,
        bass: String? = null,
    ) = SerializableChordSymbol(kind = kind, root = root, kindText = kindText, degrees = degrees, bass = bass)

    private fun properties(input: SerializableChordSymbol) =
        input
            .validate()
            .shouldBeLeft()
            .map { it.validationErrorForProperty.value }

    @Test
    fun `all 21 note name spellings validate`() {
        NoteName.entries shouldHaveSize 21
        NoteName.entries.forEach { NoteName.validate(it.name).shouldBeRight() shouldBe it }
        listOf("C_FLAT", "F_FLAT", "E_SHARP", "B_SHARP").forEach { NoteName.validate(it).shouldBeRight() }
    }

    @Test
    fun `chord kind has 33 distinct entries with their MusicXML names`() {
        ChordKind.entries shouldHaveSize 33
        ChordKind.entries.map { it.musicXmlName }.toSet() shouldHaveSize 33
        ChordKind.MINOR_SEVENTH.musicXmlName shouldBe "minor-seventh"
        ChordKind.DOMINANT_11TH.musicXmlName shouldBe "dominant-11th"
        ChordKind.NEAPOLITAN.musicXmlName shouldBe "Neapolitan"
        ChordKind.entries.forEach { ChordKind.validate(it.name).shouldBeRight() shouldBe it }
    }

    @Test
    fun `an unknown chord kind is a validation error`() {
        properties(symbol("minor-seventh")) shouldBe listOf("ChordKind")
        properties(symbol("")) shouldBe listOf("ChordKind")
    }

    @Test
    fun `degree value outside 2 to 13 is a validation error`() {
        ChordDegree
            .validate(1, 0, "ADD")
            .shouldBeLeft()
            .single()
            .validationErrorForProperty.value shouldBe
            "DegreeValue"
        ChordDegree
            .validate(14, 0, "ADD")
            .shouldBeLeft()
            .single()
            .validationErrorForProperty.value shouldBe
            "DegreeValue"
        ChordDegree.validate(2, 0, "ADD").shouldBeRight()
        ChordDegree.validate(13, 0, "ADD").shouldBeRight()
    }

    @Test
    fun `degree alter outside minus 2 to 2 is a validation error`() {
        ChordDegree
            .validate(5, -3, "ALTER")
            .shouldBeLeft()
            .single()
            .validationErrorForProperty.value shouldBe
            "DegreeAlter"
        ChordDegree
            .validate(5, 3, "ALTER")
            .shouldBeLeft()
            .single()
            .validationErrorForProperty.value shouldBe
            "DegreeAlter"
        ChordDegree.validate(5, -2, "ALTER").shouldBeRight()
        ChordDegree.validate(5, 2, "ALTER").shouldBeRight()
    }

    @Test
    fun `an unknown degree type is a validation error and degree errors accumulate`() {
        ChordDegree
            .validate(9, 0, "add")
            .shouldBeLeft()
            .single()
            .validationErrorForProperty.value shouldBe "DegreeType"
        ChordDegree.validate(20, 5, "x").shouldBeLeft().map { it.validationErrorForProperty.value } shouldBe
            listOf("DegreeValue", "DegreeAlter", "DegreeType")
    }

    @Test
    fun `root is required unless the kind is NONE`() {
        properties(symbol("MAJOR", root = null)) shouldBe listOf("ChordSymbol")
        properties(symbol("NONE", root = "C")) shouldBe listOf("ChordSymbol")
        symbol("NONE", root = null).validate().shouldBeRight()
    }

    @Test
    fun `bass is not allowed for NONE`() {
        properties(symbol("NONE", root = null, bass = "G")) shouldBe listOf("ChordSymbol")
    }

    @Test
    fun `unknown root and bass are validation errors`() {
        properties(symbol("MAJOR", root = "H", bass = "X")) shouldBe listOf("NoteName", "NoteName")
    }

    @Test
    fun `kind text is at most 32 characters`() {
        symbol("MAJOR", kindText = "a".repeat(32)).validate().shouldBeRight()
        properties(symbol("MAJOR", kindText = "a".repeat(33))) shouldBe listOf("KindText")
    }

    @Test
    fun `invalid degrees of a symbol are validation errors`() {
        properties(symbol("MAJOR", degrees = listOf(SerializableChordDegree(1, 0, "ADD")))) shouldBe
            listOf("DegreeValue")
    }

    @Test
    fun `errors of different rules accumulate`() {
        properties(symbol("bogus", root = "H", kindText = "a".repeat(40), bass = "Q")) shouldBe
            listOf("NoteName", "ChordKind", "KindText", "NoteName")
    }

    private fun roundTrip(input: SerializableChordSymbol) {
        val json = AmiMusicSdk.json
        val text = json.encodeToString(SerializableChordSymbol.serializer(), input)
        val read = json.decodeFromString(SerializableChordSymbol.serializer(), text)
        val model = read.validate().getOrElse { throw RuntimeException(it.toString()) }
        json.encodeToString(SerializableChordSymbol.serializer(), model.toSerializable()) shouldBe text
    }

    @Test
    fun `C Am7 with kind text C over G Cadd9 and N dot C round trip`() {
        roundTrip(symbol("MAJOR"))
        roundTrip(symbol("MINOR_SEVENTH", root = "A", kindText = "-7"))
        roundTrip(symbol("MAJOR", bass = "G"))
        roundTrip(symbol("MAJOR", degrees = listOf(SerializableChordDegree(9, 0, "ADD"))))
        roundTrip(symbol("NONE", root = null))
    }

    @Test
    fun `absent kind text and empty degrees are written as null`() {
        val model = symbol("MAJOR", degrees = emptyList()).validate().shouldBeRight()
        model.toSerializable() shouldBe symbol("MAJOR")
        AmiMusicSdk.json.encodeToString(SerializableChordSymbol.serializer(), model.toSerializable()) shouldBe
            """{"kind":"MAJOR","root":"C"}"""
    }
}
