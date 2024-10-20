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

import kotlinx.serialization.Serializable

@Serializable public data class Key(val todo: String? = null)

@Serializable
public data class Measure(val attributes: MeasureAttributes? = null, val notes: List<Note>)

@Serializable public data class MeasureAttributes(val key: Key? = null)

@Serializable
public data class NoteAttributes(
    val attack: Float? = null,
    val dynamics: Float? = null,
    val endDynamics: Float? = null,
    val release: Float? = null,
)

@Serializable
public data class Part(
    val id: String,
    val name: String? = null,
    val instrument: PartInstrument? = null,
    val measures: List<Measure>,
)

@Serializable
public data class PartInstrument(
    val name: String? = null,
    val midiChannel: Byte? = null,
    val midiProgram: Byte? = null,
)

@Serializable public data class Pitch(val alter: Float, val noteName: String, val octave: Byte)

@Serializable
public data class Note(
    val type: String,
    val duration: NoteDuration,
    val noteAttributes: NoteAttributes,
    val pitch: Pitch? =
        null, // also known as rootNote in case of a chord. null when note is a rest or an
    // unpitched note.
    val pitches: List<Pitch> = emptyList() // empty list when not a chord.
)

//
// public interface Note2
//
// @Serializable
// public data class Pitched(
//    val type: String,
//    val duration: NoteDuration,
//    val noteAttributes: NoteAttributes,
//    val pitch: Pitch,
// ) : Note
//
// @Serializable
// public data class Chord(
//    val type: String,
//    val duration: NoteDuration,
//    val noteAttributes: NoteAttributes,
//    val pitches: List<Pitch>,
//    val rootNote: Pitch,
// ) : Note
//
// @Serializable
// public data class Rest(
//    val type: String,
//    val duration: NoteDuration,
//    val noteAttributes: NoteAttributes
// ) : Note
//
// @Serializable
// public data class Unpitched(
//    val type: String,
//    val duration: NoteDuration,
//    val noteAttributes: NoteAttributes
// ) : Note

@Serializable public data class NoteDuration(val noteValue: String, val modifier: String)

@Serializable
public data class Score(val id: String, val title: String? = null, val parts: List<Part>)
