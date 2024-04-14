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

package com.sparetimedevs.ami.music.example

import com.sparetimedevs.ami.music.data.kotlin.note.Note
import com.sparetimedevs.ami.music.data.kotlin.note.NoteAttributes
import com.sparetimedevs.ami.music.data.kotlin.note.NoteDuration
import com.sparetimedevs.ami.music.data.kotlin.note.NoteName
import com.sparetimedevs.ami.music.data.kotlin.note.NoteValue
import com.sparetimedevs.ami.music.data.kotlin.note.Octave
import com.sparetimedevs.ami.music.data.kotlin.note.Pitch

internal fun createNote(
    noteName: NoteName,
    octave: Octave = Octave.unsafeCreate(4),
    duration: NoteValue
): Note.Pitched =
    Note.Pitched(
        duration = NoteDuration(duration),
        noteAttributes = NoteAttributes(null, null, null, null),
        pitch = Pitch(noteName = noteName, octave = octave)
    )

internal fun createChord(rootNote: Pitch, pitches: List<Pitch>, duration: NoteValue): Note.Chord =
    Note.Chord(
        duration = NoteDuration(duration),
        noteAttributes = NoteAttributes(null, null, null, null),
        rootNote = rootNote,
        pitches = pitches
    )

internal fun createRestNote(duration: NoteValue): Note.Rest =
    Note.Rest(
        duration = NoteDuration(duration),
        noteAttributes = NoteAttributes(null, null, null, null)
    )
