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

package com.sparetimedevs.ami.music.input

import com.sparetimedevs.ami.music.data.kotlin.note.Note

/**
 * `score.toInput()` why do we need this? Because even if you have the possibility to use common
 * data models, when making a request from client to service. The common data models need to be
 * converted to input models. They are input to the service being called. And the input needs to be
 * validated in that service.
 *
 * Think for instance of the use case of a web client application and a backend service.
 */
public fun com.sparetimedevs.ami.music.data.kotlin.score.Score.toInput(): Score =
    Score(
        id = this.id.value,
        title = this.title?.value,
        parts = this.parts.map { part -> part.toInput() }
    )

public fun com.sparetimedevs.ami.music.data.kotlin.part.Part.toInput(): Part =
    Part(
        id = this.id.value,
        name = this.name?.value,
        instrument = this.instrument?.toInput(),
        measures = this.measures.map { it.toInput() }
    )

public fun com.sparetimedevs.ami.music.data.kotlin.part.PartInstrument.toInput(): PartInstrument =
    PartInstrument(
        name = this.name?.value,
        midiChannel = this.midiChannel?.value,
        midiProgram = this.midiProgram?.value
    )

public fun com.sparetimedevs.ami.music.data.kotlin.measure.Measure.toInput(): Measure =
    Measure(attributes = this.attributes.toInput(), notes = this.notes.map { it.toInput() })

public fun com.sparetimedevs.ami.music.data.kotlin.measure.MeasureAttributes?.toInput():
    MeasureAttributes =
    MeasureAttributes(
        key = null // TODO
    )

public fun Note.toInput(): Any =
    when (this) {
        is Note.Pitched -> {
            Pitched(
                duration = this.duration.toInput(),
                noteAttributes = this.noteAttributes.toInput(),
                pitch = this.pitch.toInput()
            )
        }
        is Note.Chord -> {
            Chord(
                duration = this.duration.toInput(),
                noteAttributes = this.noteAttributes.toInput(),
                rootNote = this.rootNote.toInput(),
                pitches = this.pitches.map { it.toInput() }
            )
        }
        is Note.Rest -> {
            Rest(
                duration = this.duration.toInput(),
                noteAttributes = this.noteAttributes.toInput()
            )
        }
        is Note.Unpitched -> {
            Unpitched(
                duration = this.duration.toInput(),
                noteAttributes = this.noteAttributes.toInput()
            )
        }
    }

public fun com.sparetimedevs.ami.music.data.kotlin.note.NoteDuration.toInput(): NoteDuration =
    NoteDuration(noteValue = this.noteValue.name, modifier = this.modifier.name)

public fun com.sparetimedevs.ami.music.data.kotlin.note.NoteAttributes.toInput(): NoteAttributes =
    NoteAttributes(
        attack = this.attack,
        dynamics = this.dynamics,
        endDynamics = this.endDynamics,
        release = this.release
    )

public fun com.sparetimedevs.ami.music.data.kotlin.note.Pitch.toInput(): Pitch =
    Pitch(alter = this.alter.value, noteName = this.noteName.name, octave = this.octave.value)
