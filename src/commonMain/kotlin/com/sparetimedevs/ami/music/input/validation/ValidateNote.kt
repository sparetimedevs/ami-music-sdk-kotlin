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
import com.sparetimedevs.ami.core.validation.NoValidationIdentifier
import com.sparetimedevs.ami.core.validation.ValidationError
import com.sparetimedevs.ami.core.validation.ValidationIdentifier
import com.sparetimedevs.ami.core.validation.combineAllValidationErrors
import com.sparetimedevs.ami.core.validation.validationErrorForProperty
import com.sparetimedevs.ami.music.data.kotlin.note.Note
import com.sparetimedevs.ami.music.data.kotlin.note.Note.Chord
import com.sparetimedevs.ami.music.data.kotlin.note.Note.Pitched
import com.sparetimedevs.ami.music.data.kotlin.note.Note.Rest
import com.sparetimedevs.ami.music.data.kotlin.note.Note.Unpitched
import com.sparetimedevs.ami.music.data.kotlin.note.NoteAttributes
import com.sparetimedevs.ami.music.data.kotlin.note.NoteDuration
import com.sparetimedevs.ami.music.data.kotlin.note.NoteModifier
import com.sparetimedevs.ami.music.data.kotlin.note.NoteName
import com.sparetimedevs.ami.music.data.kotlin.note.NoteValue
import com.sparetimedevs.ami.music.data.kotlin.note.Octave
import com.sparetimedevs.ami.music.data.kotlin.note.Pitch
import com.sparetimedevs.ami.music.data.kotlin.note.Semitones

public fun validateNote(
    index: Int,
    input: Any,
    validationIdentifier: ValidationIdentifier = NoValidationIdentifier
): EitherNel<ValidationError, Note> =
    ValidationIdentifierForNote(index, validationIdentifier).let { validationIdentifierForNote ->
        when (input) {
            is com.sparetimedevs.ami.music.input.Pitched ->
                input.validate(validationIdentifierForNote)
            is com.sparetimedevs.ami.music.input.Chord ->
                input.validate(validationIdentifierForNote)
            is com.sparetimedevs.ami.music.input.Rest ->
                input.validate(validationIdentifierForNote)
            is com.sparetimedevs.ami.music.input.Unpitched ->
                input.validate(validationIdentifierForNote)
            else ->
                ValidationError(
                        "Note can't be of type ${input::class.simpleName}",
                        validationErrorForProperty<Note>(),
                        validationIdentifierForNote
                    )
                    .leftNel()
        }
    }

public fun com.sparetimedevs.ami.music.input.Pitched.validate(
    validationIdentifier: ValidationIdentifier = NoValidationIdentifier
): EitherNel<ValidationError, Pitched> =
    Either.zipOrAccumulate(
        this.duration.validate(validationIdentifier),
        this.noteAttributes.validate(validationIdentifier),
        this.pitch.validate(validationIdentifier)
    ) { duration, noteAttributes, pitch ->
        Pitched(duration, noteAttributes, pitch)
    }

public fun com.sparetimedevs.ami.music.input.Chord.validate(
    validationIdentifier: ValidationIdentifier = NoValidationIdentifier
): EitherNel<ValidationError, Chord> =
    Either.zipOrAccumulate(
        this.duration.validate(validationIdentifier),
        this.noteAttributes.validate(validationIdentifier),
        this.rootNote.validate(validationIdentifier),
        this.pitches
            .withIndex()
            .map { (index, pitch) -> pitch.validate(index, validationIdentifier) }
            .combineAllValidationErrors()
    ) { duration, noteAttributes, rootNote, pitches ->
        Chord(duration, noteAttributes, rootNote, pitches)
    }

public fun com.sparetimedevs.ami.music.input.Rest.validate(
    validationIdentifier: ValidationIdentifier = NoValidationIdentifier
): EitherNel<ValidationError, Rest> =
    Either.zipOrAccumulate(
        this.duration.validate(validationIdentifier),
        this.noteAttributes.validate(validationIdentifier)
    ) { duration, noteAttributes ->
        Rest(duration, noteAttributes)
    }

public fun com.sparetimedevs.ami.music.input.Unpitched.validate(
    validationIdentifier: ValidationIdentifier = NoValidationIdentifier
): EitherNel<ValidationError, Unpitched> =
    Either.zipOrAccumulate(
        this.duration.validate(validationIdentifier),
        this.noteAttributes.validate(validationIdentifier)
    ) { duration, noteAttributes ->
        Unpitched(duration, noteAttributes)
    }

public fun com.sparetimedevs.ami.music.input.NoteDuration.validate(
    validationIdentifier: ValidationIdentifier = NoValidationIdentifier
): EitherNel<ValidationError, NoteDuration> =
    Either.zipOrAccumulate(
        NoteValue.validate(this.noteValue, validationIdentifier),
        NoteModifier.validate(this.modifier, validationIdentifier)
    ) { noteValue, modifier ->
        NoteDuration(noteValue, modifier)
    }

public fun com.sparetimedevs.ami.music.input.NoteAttributes.validate(
    validationIdentifier: ValidationIdentifier = NoValidationIdentifier
): EitherNel<ValidationError, NoteAttributes> {
    // TODO implement
    return NoteAttributes(null, null, null, null).right()
}

public fun com.sparetimedevs.ami.music.input.Pitch.validate(
    validationIdentifier: ValidationIdentifier = NoValidationIdentifier
): EitherNel<ValidationError, Pitch> =
    Either.zipOrAccumulate(
        NoteName.validate(this.noteName, validationIdentifier),
        Octave.validate(this.octave, validationIdentifier),
        Semitones.validate(this.alter, validationIdentifier)
    ) { noteName, octave, alter ->
        Pitch(noteName, octave, alter)
    }

public fun com.sparetimedevs.ami.music.input.Pitch.validate(
    index: Int,
    validationIdentifier: ValidationIdentifier = NoValidationIdentifier
): EitherNel<ValidationError, Pitch> =
    ValidationIdentifierForPitchOfChord(index, validationIdentifier).let {
        validationIdentifierForPitchOfChord ->
        Either.zipOrAccumulate(
            NoteName.validate(this.noteName, validationIdentifierForPitchOfChord),
            Octave.validate(this.octave, validationIdentifierForPitchOfChord),
            Semitones.validate(this.alter, validationIdentifierForPitchOfChord)
        ) { noteName, octave, alter ->
            Pitch(noteName, octave, alter)
        }
    }
