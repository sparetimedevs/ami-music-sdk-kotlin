/*
 * Copyright (c) 2024 sparetimedevs and respective authors and developers.
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
        override val notes: List<Note>
    ) : Measure

    @Serializable
    public data class IncompleteMeasure(
        override val timeSignature: TimeSignature,
        override val attributes: MeasureAttributes?,
        override val notes: List<Note>
    ) : Measure

    @Serializable
    public data class OverfloodedMeasure(
        override val timeSignature: TimeSignature,
        override val attributes: MeasureAttributes?,
        override val notes: List<Note>,
        val overfloodedNotes: List<Note> // TODO or should this be its own type? OverfloodedNotes.
    ) : Measure

    public companion object {
        public operator fun invoke(
            attributes: MeasureAttributes?,
            notes: List<Note>,
            timeSignature: TimeSignature =
                TimeSignature(
                    Beats.validate(4).getOrNull()!!,
                    BeatType.validate(4).getOrNull()!!
                ), // TODO remove this default, and change order.
        ): Measure =
            when {
                notesShouldBeInAccordanceWithTimeSignature(notes, timeSignature) ->
                    CompleteMeasure(timeSignature, attributes, notes)
                notesAreTooManyForTimeSignature(notes, timeSignature) ->
                    OverfloodedMeasure(
                        timeSignature,
                        attributes,
                        notes /* TODO split notes */,
                        notes
                    )
                else -> IncompleteMeasure(timeSignature, attributes, notes)
            }

        private fun notesShouldBeInAccordanceWithTimeSignature(
            notes: List<Note>,
            timeSignature: TimeSignature
        ): Boolean = true /* TODO implement*/

        private fun notesAreTooManyForTimeSignature(
            notes: List<Note>,
            timeSignature: TimeSignature
        ): Boolean = false /* TODO implement*/
    }
}
