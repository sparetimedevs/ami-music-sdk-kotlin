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

package com.sparetimedevs.ami.music.serialization

/**
 * `score.toSerializable()` why do we need this? Because even if you have the possibility to use common
 * data models, when making a request from client to service. The common data models need to be
 * converted to input models. They are input to the service being called. And the input needs to be
 * validated in that service.
 *
 * Think for instance of the use case of a web client application and a backend service.
 */
public fun com.sparetimedevs.ami.music.data.kotlin.score.Score.toSerializable(): Score =
    Score(
        id = this.id.value,
        title = this.title?.value,
        parts = this.parts.map { part -> part.toSerializable() },
    )

public fun com.sparetimedevs.ami.music.data.kotlin.part.Part.toSerializable(): Part =
    Part(
        id = this.id.value,
        name = this.name?.value,
        instrument = this.instrument?.toSerializable(),
        measures = this.measures.map { it.toSerializable() },
    )

public fun com.sparetimedevs.ami.music.data.kotlin.part.PartInstrument.toSerializable(): PartInstrument =
    PartInstrument(
        name = this.name?.value,
        midiChannel = this.midiChannel?.value?.toInt(),
        midiProgram = this.midiProgram?.value?.toInt(),
    )

public fun com.sparetimedevs.ami.music.data.kotlin.measure.Measure.toSerializable(): Measure =
    Measure(attributes = this.attributes.toSerializable(), notes = this.notes.map { it.toSerializable() })

public fun com.sparetimedevs.ami.music.data.kotlin.measure.MeasureAttributes?.toSerializable(): MeasureAttributes =
    MeasureAttributes(
        key = null, // TODO
    )

// TODO this should yield either Pitched, Unpitched, Chord, or Rest.
public fun com.sparetimedevs.ami.music.data.kotlin.note.Note.toSerializable(): Pitched =
    when (this) {
        is com.sparetimedevs.ami.music.data.kotlin.note.Note.Pitched -> {
            Pitched(
                duration = this.duration.toSerializable(),
                noteAttributes = this.noteAttributes.toSerializable(),
                pitch = this.pitch.toSerializable(),
            )
        }

        is com.sparetimedevs.ami.music.data.kotlin.note.Note.Chord -> TODO()
        is com.sparetimedevs.ami.music.data.kotlin.note.Note.Rest -> TODO()
        is com.sparetimedevs.ami.music.data.kotlin.note.Note.Unpitched -> TODO()
    }

public fun com.sparetimedevs.ami.music.data.kotlin.note.NoteDuration.toSerializable(): NoteDuration =
    NoteDuration(noteValue = this.noteValue.name, modifier = this.modifier.name)

public fun com.sparetimedevs.ami.music.data.kotlin.note.NoteAttributes.toSerializable(): NoteAttributes =
    NoteAttributes(
        attack = this.attack,
        dynamics = this.dynamics,
        endDynamics = this.endDynamics,
        release = this.release,
    )

public fun com.sparetimedevs.ami.music.data.kotlin.note.Pitch.toSerializable(): Pitch =
    Pitch(
        alter = this.alter.value,
        noteName = this.noteName.name,
        octave = this.octave.value.toInt(),
    )
