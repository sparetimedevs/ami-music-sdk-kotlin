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

import com.sparetimedevs.ami.music.data.kotlin.note.Note
import com.sparetimedevs.ami.music.data.kotlin.timesignature.BeatType
import com.sparetimedevs.ami.music.data.kotlin.timesignature.Beats
import com.sparetimedevs.ami.music.data.kotlin.timesignature.TimeSignature
import kotlinx.serialization.Serializable

@Serializable
public sealed interface Measure {
    public val timeSignature: TimeSignature
    public val attributes: MeasureAttributes?
    public val notes: List<Note>

    @Serializable
    public data class CompleteMeasure(
        override val timeSignature: TimeSignature,
        override val attributes: MeasureAttributes?,
        override val notes: List<Note>,
    ) : Measure

    @Serializable
    public data class IncompleteMeasure(
        override val timeSignature: TimeSignature,
        override val attributes: MeasureAttributes?,
        override val notes: List<Note>,
    ) : Measure

    @Serializable
    public data class OverfloodedMeasure(
        override val timeSignature: TimeSignature,
        override val attributes: MeasureAttributes?,
        override val notes: List<Note>,
        val overfloodedNotes: List<Note>, // TODO or should this be its own type? OverfloodedNotes.
    ) : Measure

    public companion object {
        /**
         * Creates a measure whose [Measure.timeSignature] never contradicts its [Measure.attributes]: it is
         * `attributes.time` when stated, and otherwise [previousTimeSignature], the one of the previous measure in the
         * part. A part's first measure has no previous one ([previousTimeSignature] is `null`), and gets 4/4 in its
         * attributes when it doesn't state a time signature (D4). Empty attributes are the same as no attributes.
         */
        public operator fun invoke(
            attributes: MeasureAttributes?,
            notes: List<Note>,
            previousTimeSignature: TimeSignature?,
        ): Measure {
            val timeSignature = attributes?.time ?: previousTimeSignature ?: DEFAULT_TIME_SIGNATURE
            val resolvedAttributes =
                when {
                    attributes?.time == null && previousTimeSignature == null ->
                        (attributes ?: MeasureAttributes()).copy(time = timeSignature)
                    else -> attributes?.takeUnless { it.isEmpty() }
                }
            return when {
                notesShouldBeInAccordanceWithTimeSignature(notes, timeSignature) ->
                    CompleteMeasure(timeSignature, resolvedAttributes, notes)
                notesAreTooManyForTimeSignature(notes, timeSignature) ->
                    OverfloodedMeasure(
                        timeSignature,
                        resolvedAttributes,
                        notes /* TODO split notes */,
                        notes,
                    )
                else -> IncompleteMeasure(timeSignature, resolvedAttributes, notes)
            }
        }

        private val DEFAULT_TIME_SIGNATURE: TimeSignature =
            TimeSignature(Beats.unsafeCreate(4), BeatType.unsafeCreate(4))

        private fun notesShouldBeInAccordanceWithTimeSignature(
            notes: List<Note>,
            timeSignature: TimeSignature,
        ): Boolean = true // TODO implement

        private fun notesAreTooManyForTimeSignature(
            notes: List<Note>,
            timeSignature: TimeSignature,
        ): Boolean = false // TODO implement
    }
}
