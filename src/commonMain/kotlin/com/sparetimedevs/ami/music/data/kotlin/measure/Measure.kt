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

import com.sparetimedevs.ami.music.data.kotlin.fraction.Fraction
import com.sparetimedevs.ami.music.data.kotlin.harmony.Harmony
import com.sparetimedevs.ami.music.data.kotlin.lyric.LyricFragment
import com.sparetimedevs.ami.music.data.kotlin.note.Note
import com.sparetimedevs.ami.music.data.kotlin.section.SectionStart
import com.sparetimedevs.ami.music.data.kotlin.textdirection.TextDirection
import com.sparetimedevs.ami.music.data.kotlin.timesignature.TimeSignature
import kotlinx.serialization.Serializable

@Serializable
public sealed interface Measure {
    public val timeSignature: TimeSignature
    public val attributes: MeasureAttributes?
    public val notes: List<Note>

    /** The length of the first measure of a part when it is shorter than [timeSignature] says (D5), otherwise `null`. */
    public val pickup: Fraction?

    /** Chord symbols by position (F9). Offsets strictly increase and lie within [length]. */
    public val harmonies: List<Harmony>

    /** Lyric fragments by position and verse (D7). Offsets strictly increase within each verse. */
    public val lyrics: List<LyricFragment>

    /** Text directions by position, in order. Offsets don't decrease and lie within `0 ≤ offset ≤ length`. */
    public val textDirections: List<TextDirection>

    /** Offsets where a new line starts. Strictly increasing, `0 ≤ offset < length` (D6). */
    public val lineBreaks: List<Fraction>

    /** Where a new section (verse, chorus) starts (D15). Offsets strictly increase, `0 ≤ offset < length`. */
    public val sectionStarts: List<SectionStart>

    /** `pickup ?: timeSignature.measureLength`, as a fraction of a whole note. */
    public val length: Fraction get() = pickup ?: timeSignature.measureLength

    @Serializable
    public data class CompleteMeasure(
        override val timeSignature: TimeSignature,
        override val attributes: MeasureAttributes?,
        override val notes: List<Note>,
        override val pickup: Fraction? = null,
        override val harmonies: List<Harmony> = emptyList(),
        override val lyrics: List<LyricFragment> = emptyList(),
        override val textDirections: List<TextDirection> = emptyList(),
        override val lineBreaks: List<Fraction> = emptyList(),
        override val sectionStarts: List<SectionStart> = emptyList(),
    ) : Measure

    @Serializable
    public data class IncompleteMeasure(
        override val timeSignature: TimeSignature,
        override val attributes: MeasureAttributes?,
        override val notes: List<Note>,
        override val pickup: Fraction? = null,
        override val harmonies: List<Harmony> = emptyList(),
        override val lyrics: List<LyricFragment> = emptyList(),
        override val textDirections: List<TextDirection> = emptyList(),
        override val lineBreaks: List<Fraction> = emptyList(),
        override val sectionStarts: List<SectionStart> = emptyList(),
    ) : Measure

    @Serializable
    public data class OverfloodedMeasure(
        override val timeSignature: TimeSignature,
        override val attributes: MeasureAttributes?,
        override val notes: List<Note>,
        val overfloodedNotes: List<Note>, // TODO or should this be its own type? OverfloodedNotes.
        override val pickup: Fraction? = null,
        override val harmonies: List<Harmony> = emptyList(),
        override val lyrics: List<LyricFragment> = emptyList(),
        override val textDirections: List<TextDirection> = emptyList(),
        override val lineBreaks: List<Fraction> = emptyList(),
        override val sectionStarts: List<SectionStart> = emptyList(),
    ) : Measure

    public companion object {
        /**
         * Creates a measure with the [timeSignature] that is in effect for it. The time signature is stored once, on the
         * measure itself; [MeasureAttributes] don't repeat it. Whether a time signature is stated in the serialized form
         * (the first measure of a part, and wherever it changes) is decided when serializing. Empty attributes are the
         * same as no attributes.
         */
        public operator fun invoke(
            attributes: MeasureAttributes?,
            notes: List<Note>,
            timeSignature: TimeSignature,
            pickup: Fraction? = null,
            harmonies: List<Harmony> = emptyList(),
            lyrics: List<LyricFragment> = emptyList(),
            textDirections: List<TextDirection> = emptyList(),
            lineBreaks: List<Fraction> = emptyList(),
            sectionStarts: List<SectionStart> = emptyList(),
        ): Measure {
            val resolvedAttributes = attributes?.takeUnless { it.isEmpty() }
            return when {
                notesShouldBeInAccordanceWithTimeSignature(notes, timeSignature) ->
                    CompleteMeasure(
                        timeSignature,
                        resolvedAttributes,
                        notes,
                        pickup,
                        harmonies,
                        lyrics,
                        textDirections,
                        lineBreaks,
                        sectionStarts,
                    )
                notesAreTooManyForTimeSignature(notes, timeSignature) ->
                    OverfloodedMeasure(
                        timeSignature,
                        resolvedAttributes,
                        notes /* TODO split notes */,
                        notes,
                        pickup,
                        harmonies,
                        lyrics,
                        textDirections,
                        lineBreaks,
                        sectionStarts,
                    )
                else ->
                    IncompleteMeasure(
                        timeSignature,
                        resolvedAttributes,
                        notes,
                        pickup,
                        harmonies,
                        lyrics,
                        textDirections,
                        lineBreaks,
                        sectionStarts,
                    )
            }
        }

        private fun notesShouldBeInAccordanceWithTimeSignature(
            notes: List<Note>,
            timeSignature: TimeSignature,
        ): Boolean = true // TODO implement. A measure without notes is always complete, whatever its length (F5).

        private fun notesAreTooManyForTimeSignature(
            notes: List<Note>,
            timeSignature: TimeSignature,
        ): Boolean = false // TODO implement
    }
}
