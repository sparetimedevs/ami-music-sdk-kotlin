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

package com.sparetimedevs.ami.music.example

import com.sparetimedevs.ami.core.validation.getOrThrowFirstValidationError
import com.sparetimedevs.ami.music.data.kotlin.chord.ChordDegree
import com.sparetimedevs.ami.music.data.kotlin.chord.ChordKind
import com.sparetimedevs.ami.music.data.kotlin.chord.ChordSymbol
import com.sparetimedevs.ami.music.data.kotlin.chord.DegreeAlter
import com.sparetimedevs.ami.music.data.kotlin.chord.DegreeType
import com.sparetimedevs.ami.music.data.kotlin.chord.DegreeValue
import com.sparetimedevs.ami.music.data.kotlin.chord.KindText
import com.sparetimedevs.ami.music.data.kotlin.fraction.Fraction
import com.sparetimedevs.ami.music.data.kotlin.harmony.Harmony
import com.sparetimedevs.ami.music.data.kotlin.key.Fifths
import com.sparetimedevs.ami.music.data.kotlin.key.Key
import com.sparetimedevs.ami.music.data.kotlin.key.KeyMode
import com.sparetimedevs.ami.music.data.kotlin.lyric.LyricFragment
import com.sparetimedevs.ami.music.data.kotlin.measure.Measure
import com.sparetimedevs.ami.music.data.kotlin.measure.MeasureAttributes
import com.sparetimedevs.ami.music.data.kotlin.midi.MidiChannel
import com.sparetimedevs.ami.music.data.kotlin.midi.MidiProgram
import com.sparetimedevs.ami.music.data.kotlin.note.NoteName
import com.sparetimedevs.ami.music.data.kotlin.part.Part
import com.sparetimedevs.ami.music.data.kotlin.part.PartId
import com.sparetimedevs.ami.music.data.kotlin.part.PartInstrument
import com.sparetimedevs.ami.music.data.kotlin.part.PartInstrumentName
import com.sparetimedevs.ami.music.data.kotlin.part.PartName
import com.sparetimedevs.ami.music.data.kotlin.score.Score
import com.sparetimedevs.ami.music.data.kotlin.score.ScoreId
import com.sparetimedevs.ami.music.data.kotlin.score.ScoreTitle
import com.sparetimedevs.ami.music.data.kotlin.section.SectionStart
import com.sparetimedevs.ami.music.data.kotlin.tempo.QuarterNotesPerMinute
import com.sparetimedevs.ami.music.data.kotlin.tempo.Tempo
import com.sparetimedevs.ami.music.data.kotlin.textdirection.TextDirection

private val ZERO = Fraction.ZERO
private val QUARTER = Fraction.of(1, 4)
private val HALF = Fraction.of(1, 2)
private val THREE_QUARTERS = Fraction.of(3, 4)

private fun chord(
    root: NoteName,
    kind: ChordKind,
    kindText: String? = null,
    degrees: List<ChordDegree> = emptyList(),
    bass: NoteName? = null,
): ChordSymbol =
    ChordSymbol(root, kind, kindText?.let { KindText.validate(it).getOrThrowFirstValidationError() }, degrees, bass)

private val C_MAJOR = chord(NoteName.C, ChordKind.MAJOR)
private val G_SEVENTH = chord(NoteName.G, ChordKind.DOMINANT)
private val F_MAJOR = chord(NoteName.F, ChordKind.MAJOR)
private val C_ADD_NINE =
    chord(
        NoteName.C,
        ChordKind.MAJOR,
        kindText = "add9",
        degrees = listOf(ChordDegree(DegreeValue.unsafeCreate(9), DegreeAlter.unsafeCreate(0), DegreeType.ADD)),
    )
private val C_OVER_G = chord(NoteName.C, ChordKind.MAJOR, bass = NoteName.G)
private val NO_CHORD = ChordSymbol(root = null, kind = ChordKind.NONE)

/** The same text at the same offset in verse 1 and verse 2. */
private fun lyrics(
    offset: Fraction,
    verse1: String,
    verse2: String,
): List<LyricFragment> = listOf(LyricFragment(offset, verse1, 1), LyricFragment(offset, verse2, 2))

/**
 * "When the Saints Go Marching In": a traditional spiritual, in the public domain. A lead sheet with chords and two
 * verses, without notes. It covers a pickup, a half-measure and a beat-4 chord change, chords-only measures, a text
 * direction, a line break inside a measure, a labelled section start, an N.C., a slash chord and an add9.
 */
public fun getExampleScoreWhenTheSaintsGoMarchingIn(): Score {
    val measures =
        listOf(
            Measure(
                attributes =
                    MeasureAttributes(
                        key = Key(Fifths.unsafeCreate(0), KeyMode.MAJOR),
                        tempo = Tempo(QuarterNotesPerMinute.unsafeCreate(100)),
                    ),
                notes = emptyList(),
                timeSignature = FOUR_FOUR,
                pickup = THREE_QUARTERS,
                harmonies = listOf(Harmony(ZERO, C_MAJOR)),
                lyrics =
                    lyrics(
                        ZERO,
                        "Oh, ",
                        "Oh, ",
                    ) + lyrics(QUARTER, "when ", "when ") + lyrics(HALF, "the ", "the "),
                textDirections = listOf(TextDirection(ZERO, "Swing")),
                sectionStarts = listOf(SectionStart(ZERO, "Verse")),
            ),
            Measure(
                attributes = null,
                notes = emptyList(),
                timeSignature = FOUR_FOUR,
                harmonies = listOf(Harmony(ZERO, C_MAJOR), Harmony(HALF, G_SEVENTH)),
                lyrics =
                    lyrics(ZERO, "saints ", "sun ") +
                        lyrics(QUARTER, "go ", "be") +
                        lyrics(HALF, "march", "gins ") +
                        lyrics(THREE_QUARTERS, "ing ", "to "),
            ),
            Measure(
                attributes = null,
                notes = emptyList(),
                timeSignature = FOUR_FOUR,
                harmonies = listOf(Harmony(ZERO, C_MAJOR), Harmony(THREE_QUARTERS, G_SEVENTH)),
                lyrics =
                    lyrics(ZERO, "in", "shine") +
                        lyrics(QUARTER, "Oh, ", "Oh, ") +
                        lyrics(HALF, "when ", "when ") +
                        lyrics(THREE_QUARTERS, "the ", "the "),
                lineBreaks = listOf(QUARTER),
            ),
            Measure(
                attributes = null,
                notes = emptyList(),
                timeSignature = FOUR_FOUR,
                harmonies = listOf(Harmony(ZERO, C_ADD_NINE), Harmony(HALF, F_MAJOR)),
                lyrics =
                    lyrics(ZERO, "saints ", "sun ") +
                        lyrics(QUARTER, "go ", "be") +
                        lyrics(HALF, "march", "gins ") +
                        lyrics(THREE_QUARTERS, "ing ", "to "),
            ),
            Measure(
                attributes = null,
                notes = emptyList(),
                timeSignature = FOUR_FOUR,
                harmonies = listOf(Harmony(ZERO, C_OVER_G), Harmony(HALF, NO_CHORD)),
                lyrics = lyrics(ZERO, "in", "shine"),
            ),
        )

    return Score(
        ScoreId.unsafeCreate("ce2acd90-dfa0-4bfd-8ba0-ae1c00c7b7ec"),
        ScoreTitle.unsafeCreate("When the Saints Go Marching In"),
        listOf(
            Part(
                id = PartId.unsafeCreate("p-1"),
                name = PartName.unsafeCreate("Part one"),
                instrument =
                    PartInstrument(
                        name = PartInstrumentName.unsafeCreate("Grand Piano"),
                        midiChannel = MidiChannel.unsafeCreate(0),
                        midiProgram = MidiProgram.unsafeCreate(1),
                    ),
                measures = measures,
            ),
        ),
    )
}
