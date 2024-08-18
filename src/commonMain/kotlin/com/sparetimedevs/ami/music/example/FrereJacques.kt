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

import com.sparetimedevs.ami.music.data.kotlin.midi.MidiChannel
import com.sparetimedevs.ami.music.data.kotlin.midi.MidiProgram
import com.sparetimedevs.ami.music.data.kotlin.note.NoteName
import com.sparetimedevs.ami.music.data.kotlin.note.NoteValue
import com.sparetimedevs.ami.music.data.kotlin.note.Octave
import com.sparetimedevs.ami.music.data.kotlin.part.Part
import com.sparetimedevs.ami.music.data.kotlin.part.PartId
import com.sparetimedevs.ami.music.data.kotlin.part.PartInstrument
import com.sparetimedevs.ami.music.data.kotlin.part.PartInstrumentName
import com.sparetimedevs.ami.music.data.kotlin.part.PartName
import com.sparetimedevs.ami.music.data.kotlin.score.Score
import com.sparetimedevs.ami.music.data.kotlin.score.ScoreId
import com.sparetimedevs.ami.music.data.kotlin.score.ScoreTitle

public fun getExampleScoreFrereJacques(): Score {
    val parts =
        listOf(
            Part(
                id = PartId.unsafeCreate("p-1"),
                name = PartName.unsafeCreate("Part one"),
                instrument =
                    PartInstrument(
                        name = PartInstrumentName.unsafeCreate("Grand Piano"),
                        midiChannel = MidiChannel.unsafeCreate(0),
                        midiProgram = MidiProgram.unsafeCreate(1)
                    ),
                measures =
                    listOf(
                        createMeasure(
                            createNote(noteName = NoteName.C, duration = NoteValue.QUARTER),
                            createNote(noteName = NoteName.D, duration = NoteValue.QUARTER),
                            createNote(noteName = NoteName.E, duration = NoteValue.QUARTER),
                            createNote(noteName = NoteName.C, duration = NoteValue.QUARTER)
                        ),
                        createMeasure(
                            createNote(noteName = NoteName.C, duration = NoteValue.QUARTER),
                            createNote(noteName = NoteName.D, duration = NoteValue.QUARTER),
                            createNote(noteName = NoteName.E, duration = NoteValue.QUARTER),
                            createNote(noteName = NoteName.C, duration = NoteValue.QUARTER)
                        ),
                        createMeasure(
                            createNote(noteName = NoteName.E, duration = NoteValue.QUARTER),
                            createNote(noteName = NoteName.F, duration = NoteValue.QUARTER),
                            createNote(noteName = NoteName.G, duration = NoteValue.HALF)
                        ),
                        createMeasure(
                            createNote(noteName = NoteName.E, duration = NoteValue.QUARTER),
                            createNote(noteName = NoteName.F, duration = NoteValue.QUARTER),
                            createNote(noteName = NoteName.G, duration = NoteValue.HALF)
                        ),
                        createMeasure(
                            createNote(noteName = NoteName.G, duration = NoteValue._8TH),
                            createNote(
                                noteName = NoteName.A,
                                octave = Octave.unsafeCreate(5),
                                duration = NoteValue._8TH
                            ),
                            createNote(noteName = NoteName.G, duration = NoteValue._8TH),
                            createNote(noteName = NoteName.F, duration = NoteValue._8TH),
                            createNote(noteName = NoteName.E, duration = NoteValue.QUARTER),
                            createNote(noteName = NoteName.C, duration = NoteValue.QUARTER),
                        ),
                        createMeasure(
                            createNote(noteName = NoteName.G, duration = NoteValue._8TH),
                            createNote(
                                noteName = NoteName.A,
                                octave = Octave.unsafeCreate(5),
                                duration = NoteValue._8TH
                            ),
                            createNote(noteName = NoteName.G, duration = NoteValue._8TH),
                            createNote(noteName = NoteName.F, duration = NoteValue._8TH),
                            createNote(noteName = NoteName.E, duration = NoteValue.QUARTER),
                            createNote(noteName = NoteName.C, duration = NoteValue.QUARTER),
                        ),
                        createMeasure(
                            createNote(noteName = NoteName.C, duration = NoteValue.QUARTER),
                            createNote(
                                noteName = NoteName.G,
                                octave = Octave.unsafeCreate(3),
                                duration = NoteValue.QUARTER
                            ),
                            createNote(noteName = NoteName.C, duration = NoteValue.HALF)
                        ),
                        createMeasure(
                            createNote(noteName = NoteName.C, duration = NoteValue.QUARTER),
                            createNote(
                                noteName = NoteName.G,
                                octave = Octave.unsafeCreate(3),
                                duration = NoteValue.QUARTER
                            ),
                            createNote(noteName = NoteName.C, duration = NoteValue.HALF)
                        )
                    )
            )
        )

    return Score(
        ScoreId.unsafeCreate("ef23c062-90e9-4cfd-af60-41d6e453064a"),
        ScoreTitle.unsafeCreate("Frère Jacques"),
        parts
    )
}
