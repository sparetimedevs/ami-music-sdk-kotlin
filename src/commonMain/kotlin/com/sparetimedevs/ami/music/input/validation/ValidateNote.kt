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

package com.sparetimedevs.ami.music.input.validation

import arrow.core.Either
import arrow.core.EitherNel
import arrow.core.leftNel
import arrow.core.right
import com.sparetimedevs.ami.core.validation.ValidationError
import com.sparetimedevs.ami.core.validation.ValidationErrorForId
import com.sparetimedevs.ami.music.data.kotlin.note.Note
import com.sparetimedevs.ami.music.data.kotlin.note.Note.Chord
import com.sparetimedevs.ami.music.data.kotlin.note.Note.Pitched
import com.sparetimedevs.ami.music.data.kotlin.note.NoteAttributes
import com.sparetimedevs.ami.music.data.kotlin.note.NoteDuration
import com.sparetimedevs.ami.music.data.kotlin.note.NoteModifier
import com.sparetimedevs.ami.music.data.kotlin.note.NoteName
import com.sparetimedevs.ami.music.data.kotlin.note.NoteValue
import com.sparetimedevs.ami.music.data.kotlin.note.Octave
import com.sparetimedevs.ami.music.data.kotlin.note.Pitch
import com.sparetimedevs.ami.music.data.kotlin.note.Semitones

public fun validateNote(input: Any, forId: ValidationErrorForId): EitherNel<ValidationError, Note> =
    when (input) {
        is com.sparetimedevs.ami.music.input.Pitched -> input.validate(forId)
        is com.sparetimedevs.ami.music.input.Chord -> input.validate(forId)
        // TODO add Unpitched, Rest
        else -> ValidationError("Note can't be of type ${input::class.simpleName}", forId).leftNel()
    }

public fun com.sparetimedevs.ami.music.input.Pitched.validate(
    forId: ValidationErrorForId
): EitherNel<ValidationError, Pitched> =
    Either.zipOrAccumulate(
        this.duration.validate(forId),
        this.noteAttributes.validate(),
        this.pitch.validate(forId)
    ) { duration, noteAttributes, pitch ->
        Pitched(duration, noteAttributes, pitch)
    }

public fun com.sparetimedevs.ami.music.input.Chord.validate(
    forId: ValidationErrorForId
): EitherNel<ValidationError, Chord> =
    Either.zipOrAccumulate(
        this.duration.validate(forId),
        this.noteAttributes.validate(),
        this.rootNote.validate(forId),
        this.pitches.map { pitch -> pitch.validate(forId) }.combineAllValidationErrors()
    ) { duration, noteAttributes, rootNote, pitches ->
        Chord(duration, noteAttributes, rootNote, pitches)
    }

public fun com.sparetimedevs.ami.music.input.NoteDuration.validate(
    forId: ValidationErrorForId
): EitherNel<ValidationError, NoteDuration> =
    Either.zipOrAccumulate(
        NoteValue.validate(this.noteValue, forId),
        NoteModifier.validate(this.modifier)
    ) { noteValue, modifier ->
        NoteDuration(noteValue, modifier)
    }

public fun com.sparetimedevs.ami.music.input.NoteAttributes.validate():
    EitherNel<ValidationError, NoteAttributes> {
    // TODO implement
    return NoteAttributes(null, null, null, null).right()
}

public fun com.sparetimedevs.ami.music.input.Pitch.validate(
    forId: ValidationErrorForId
): EitherNel<ValidationError, Pitch> =
    Either.zipOrAccumulate(
        NoteName.validate(this.noteName, forId),
        Octave.validate(this.octave, forId),
        Semitones.validate(this.alter)
    ) { noteName, octave, alter ->
        Pitch(noteName, octave, alter)
    }
