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

import com.sparetimedevs.ami.music.data.kotlin.measure.Measure
import com.sparetimedevs.ami.music.data.kotlin.note.Note.Pitched
import com.sparetimedevs.ami.music.data.kotlin.note.NoteAttributes
import com.sparetimedevs.ami.music.data.kotlin.note.NoteDuration
import com.sparetimedevs.ami.music.data.kotlin.note.NoteName
import com.sparetimedevs.ami.music.data.kotlin.note.NoteValue
import com.sparetimedevs.ami.music.data.kotlin.note.Octave
import com.sparetimedevs.ami.music.data.kotlin.note.Pitch
import com.sparetimedevs.ami.music.data.kotlin.note.Semitones
import com.sparetimedevs.ami.music.data.kotlin.part.Part
import com.sparetimedevs.ami.music.data.kotlin.part.PartId
import com.sparetimedevs.ami.music.data.kotlin.score.Score
import com.sparetimedevs.ami.music.data.kotlin.score.ScoreId

public fun getExampleScore0(): Score {
    val parts =
        listOf(
            Part(
                id = PartId.unsafeCreate("p-1"),
                measures =
                    listOf(
                        Measure(
                            null,
                            listOf(
                                Pitched(
                                    duration = NoteDuration(noteValue = NoteValue.QUARTER),
                                    noteAttributes =
                                        NoteAttributes(
                                            attack = null,
                                            dynamics = null,
                                            endDynamics = null,
                                            release = null
                                        ),
                                    pitch =
                                        Pitch(
                                            noteName = NoteName.C,
                                            octave = Octave.unsafeCreate(input = 4),
                                            alter = Semitones.unsafeCreate(input = 0.0f)
                                        )
                                ),
                                Pitched(
                                    duration = NoteDuration(noteValue = NoteValue.QUARTER),
                                    noteAttributes =
                                        NoteAttributes(
                                            attack = null,
                                            dynamics = null,
                                            endDynamics = null,
                                            release = null
                                        ),
                                    pitch =
                                        Pitch(
                                            noteName = NoteName.G,
                                            octave = Octave.unsafeCreate(input = 4),
                                            alter = Semitones.unsafeCreate(input = 0.0f)
                                        )
                                ),
                                Pitched(
                                    duration = NoteDuration(noteValue = NoteValue.QUARTER),
                                    noteAttributes =
                                        NoteAttributes(
                                            attack = null,
                                            dynamics = null,
                                            endDynamics = null,
                                            release = null
                                        ),
                                    pitch =
                                        Pitch(
                                            noteName = NoteName.A,
                                            octave = Octave.unsafeCreate(input = 4),
                                            alter = Semitones.unsafeCreate(input = 0.0f)
                                        )
                                ),
                                Pitched(
                                    duration = NoteDuration(noteValue = NoteValue.QUARTER),
                                    noteAttributes =
                                        NoteAttributes(
                                            attack = null,
                                            dynamics = null,
                                            endDynamics = null,
                                            release = null
                                        ),
                                    pitch =
                                        Pitch(
                                            noteName = NoteName.E,
                                            octave = Octave.unsafeCreate(input = 4),
                                            alter = Semitones.unsafeCreate(input = 0.0f)
                                        )
                                )
                            )
                        ),
                        Measure(
                            attributes = null,
                            notes =
                                listOf(
                                    Pitched(
                                        duration = NoteDuration(noteValue = NoteValue.QUARTER),
                                        noteAttributes =
                                            NoteAttributes(
                                                attack = null,
                                                dynamics = null,
                                                endDynamics = null,
                                                release = null
                                            ),
                                        pitch =
                                            Pitch(
                                                noteName = NoteName.C,
                                                octave = Octave.unsafeCreate(input = 4),
                                                alter = Semitones.unsafeCreate(input = 0.0f)
                                            )
                                    ),
                                    Pitched(
                                        duration = NoteDuration(noteValue = NoteValue.QUARTER),
                                        noteAttributes =
                                            NoteAttributes(
                                                attack = null,
                                                dynamics = null,
                                                endDynamics = null,
                                                release = null
                                            ),
                                        pitch =
                                            Pitch(
                                                noteName = NoteName.C,
                                                octave = Octave.unsafeCreate(input = 4),
                                                alter = Semitones.unsafeCreate(input = 0.0f)
                                            )
                                    ),
                                    Pitched(
                                        duration = NoteDuration(noteValue = NoteValue.QUARTER),
                                        noteAttributes =
                                            NoteAttributes(
                                                attack = null,
                                                dynamics = null,
                                                endDynamics = null,
                                                release = null
                                            ),
                                        pitch =
                                            Pitch(
                                                noteName = NoteName.A,
                                                octave = Octave.unsafeCreate(input = 4),
                                                alter = Semitones.unsafeCreate(input = 0.0f)
                                            )
                                    ),
                                    Pitched(
                                        duration = NoteDuration(noteValue = NoteValue.QUARTER),
                                        noteAttributes =
                                            NoteAttributes(
                                                attack = null,
                                                dynamics = null,
                                                endDynamics = null,
                                                release = null
                                            ),
                                        pitch =
                                            Pitch(
                                                noteName = NoteName.A,
                                                octave = Octave.unsafeCreate(input = 4),
                                                alter = Semitones.unsafeCreate(input = 0.0f)
                                            )
                                    )
                                )
                        ),
                    )
            ),
            Part(
                id = PartId.unsafeCreate("p-2"),
                measures =
                    listOf(
                        Measure(
                            attributes = null,
                            notes =
                                listOf(
                                    Pitched(
                                        duration = NoteDuration(noteValue = NoteValue.QUARTER),
                                        noteAttributes =
                                            NoteAttributes(
                                                attack = null,
                                                dynamics = null,
                                                endDynamics = null,
                                                release = null
                                            ),
                                        pitch =
                                            Pitch(
                                                noteName = NoteName.C,
                                                octave = Octave.unsafeCreate(input = 4),
                                                alter = Semitones.unsafeCreate(input = 0.0f)
                                            )
                                    ),
                                    Pitched(
                                        duration = NoteDuration(noteValue = NoteValue.QUARTER),
                                        noteAttributes =
                                            NoteAttributes(
                                                attack = null,
                                                dynamics = null,
                                                endDynamics = null,
                                                release = null
                                            ),
                                        pitch =
                                            Pitch(
                                                noteName = NoteName.G,
                                                octave = Octave.unsafeCreate(input = 4),
                                                alter = Semitones.unsafeCreate(input = 0.0f)
                                            )
                                    ),
                                    Pitched(
                                        duration = NoteDuration(noteValue = NoteValue.QUARTER),
                                        noteAttributes =
                                            NoteAttributes(
                                                attack = null,
                                                dynamics = null,
                                                endDynamics = null,
                                                release = null
                                            ),
                                        pitch =
                                            Pitch(
                                                noteName = NoteName.A,
                                                octave = Octave.unsafeCreate(input = 4),
                                                alter = Semitones.unsafeCreate(input = 0.0f)
                                            )
                                    ),
                                    Pitched(
                                        duration = NoteDuration(noteValue = NoteValue.QUARTER),
                                        noteAttributes =
                                            NoteAttributes(
                                                attack = null,
                                                dynamics = null,
                                                endDynamics = null,
                                                release = null
                                            ),
                                        pitch =
                                            Pitch(
                                                noteName = NoteName.E,
                                                octave = Octave.unsafeCreate(input = 4),
                                                alter = Semitones.unsafeCreate(input = 0.0f)
                                            )
                                    )
                                )
                        ),
                        Measure(
                            attributes = null,
                            notes =
                                listOf(
                                    Pitched(
                                        duration = NoteDuration(noteValue = NoteValue.QUARTER),
                                        noteAttributes =
                                            NoteAttributes(
                                                attack = null,
                                                dynamics = null,
                                                endDynamics = null,
                                                release = null
                                            ),
                                        pitch =
                                            Pitch(
                                                noteName = NoteName.C,
                                                octave = Octave.unsafeCreate(input = 4),
                                                alter = Semitones.unsafeCreate(input = 0.0f)
                                            )
                                    ),
                                    Pitched(
                                        duration = NoteDuration(noteValue = NoteValue.QUARTER),
                                        noteAttributes =
                                            NoteAttributes(
                                                attack = null,
                                                dynamics = null,
                                                endDynamics = null,
                                                release = null
                                            ),
                                        pitch =
                                            Pitch(
                                                noteName = NoteName.C,
                                                octave = Octave.unsafeCreate(input = 4),
                                                alter = Semitones.unsafeCreate(input = 0.0f)
                                            )
                                    ),
                                    Pitched(
                                        duration = NoteDuration(noteValue = NoteValue.QUARTER),
                                        noteAttributes =
                                            NoteAttributes(
                                                attack = null,
                                                dynamics = null,
                                                endDynamics = null,
                                                release = null
                                            ),
                                        pitch =
                                            Pitch(
                                                noteName = NoteName.A,
                                                octave = Octave.unsafeCreate(input = 4),
                                                alter = Semitones.unsafeCreate(input = 0.0f)
                                            )
                                    ),
                                    Pitched(
                                        duration = NoteDuration(noteValue = NoteValue.QUARTER),
                                        noteAttributes =
                                            NoteAttributes(
                                                attack = null,
                                                dynamics = null,
                                                endDynamics = null,
                                                release = null
                                            ),
                                        pitch =
                                            Pitch(
                                                noteName = NoteName.A,
                                                octave = Octave.unsafeCreate(input = 4),
                                                alter = Semitones.unsafeCreate(input = 0.0f)
                                            )
                                    )
                                )
                        )
                    )
            )
        )

    return Score(ScoreId.unsafeCreate("d737b4ae-fbaa-4b0d-9d36-d3651e30e93a"), null, parts)
}
