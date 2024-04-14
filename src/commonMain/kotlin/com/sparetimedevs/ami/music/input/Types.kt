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

@Serializable
public data class Chord(
    val duration: NoteDuration,
    val noteAttributes: NoteAttributes,
    val pitches: List<Pitch>,
    val rootNote: Pitch
)

@Serializable public data class Key(val todo: String? = null)

@Serializable
public data class Measure(val attributes: MeasureAttributes? = null, val notes: List<Pitched>)

@Serializable public data class MeasureAttributes(val key: Key? = null)

@Serializable
public data class NoteAttributes(
    val attack: Float? = null,
    val dynamics: Float? = null,
    val endDynamics: Float? = null,
    val release: Float? = null
)

@Serializable public data class Part(val id: String, val measures: List<Measure>)

@Serializable public data class Pitch(val alter: Float, val noteName: String, val octave: Byte)

@Serializable
public data class Pitched(
    val duration: NoteDuration,
    val noteAttributes: NoteAttributes,
    val pitch: Pitch
)

@Serializable
public data class Rest(val duration: NoteDuration, val noteAttributes: NoteAttributes)

@Serializable public data class NoteDuration(val noteValue: String, val modifier: String)

@Serializable
public data class Score(val id: String, val title: String? = null, val parts: List<Part>)

@Serializable
public data class Unpitched(val duration: NoteDuration, val noteAttributes: NoteAttributes)
