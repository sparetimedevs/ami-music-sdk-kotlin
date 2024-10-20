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

public fun com.sparetimedevs.ami.music.data.kotlin.note.Note.toInput(): Note =
    when (this) {
        is com.sparetimedevs.ami.music.data.kotlin.note.Note.Pitched -> {
            Note(
                type = "pitched",
                duration = this.duration.toInput(),
                noteAttributes = this.noteAttributes.toInput(),
                pitch = this.pitch.toInput(),
                pitches = emptyList()
            )
        }
        is com.sparetimedevs.ami.music.data.kotlin.note.Note.Chord -> {
            Note(
                type = "chord",
                duration = this.duration.toInput(),
                noteAttributes = this.noteAttributes.toInput(),
                pitch = this.rootNote.toInput(),
                pitches = this.pitches.map { it.toInput() }
            )
        }
        is com.sparetimedevs.ami.music.data.kotlin.note.Note.Rest -> {
            Note(
                type = "rest",
                duration = this.duration.toInput(),
                noteAttributes = this.noteAttributes.toInput(),
                pitch = null,
                pitches = emptyList()
            )
        }
        is com.sparetimedevs.ami.music.data.kotlin.note.Note.Unpitched -> {
            Note(
                type = "unpitched",
                duration = this.duration.toInput(),
                noteAttributes = this.noteAttributes.toInput(),
                pitch = null,
                pitches = emptyList()
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
