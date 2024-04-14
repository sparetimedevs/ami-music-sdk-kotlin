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
import com.sparetimedevs.ami.music.data.kotlin.note.Note
import com.sparetimedevs.ami.music.data.kotlin.note.NoteAttributes
import com.sparetimedevs.ami.music.data.kotlin.note.NoteDuration
import com.sparetimedevs.ami.music.data.kotlin.note.NoteModifier
import com.sparetimedevs.ami.music.data.kotlin.note.NoteName
import com.sparetimedevs.ami.music.data.kotlin.note.NoteValue
import com.sparetimedevs.ami.music.data.kotlin.note.Octave
import com.sparetimedevs.ami.music.data.kotlin.note.Pitch
import com.sparetimedevs.ami.music.data.kotlin.part.Part
import com.sparetimedevs.ami.music.data.kotlin.part.PartId
import com.sparetimedevs.ami.music.data.kotlin.score.Score
import com.sparetimedevs.ami.music.data.kotlin.score.ScoreId
import com.sparetimedevs.ami.music.data.kotlin.score.ScoreTitle

public fun getExampleScoreHeighHoNobodyHome(): Score {
    val parts =
        listOf(
            Part(
                PartId.unsafeCreate("p-1"),
                listOf(
                    Measure(
                        null,
                        listOf(
                            Note.Pitched(
                                NoteDuration(NoteValue.HALF),
                                NoteAttributes(null, null, null, null),
                                Pitch(NoteName.D, Octave.unsafeCreate(4))
                            ),
                            Note.Pitched(
                                NoteDuration(NoteValue.HALF),
                                NoteAttributes(null, null, null, null),
                                Pitch(NoteName.C, Octave.unsafeCreate(4))
                            )
                        )
                    ),
                    Measure(
                        null,
                        listOf(
                            Note.Pitched(
                                NoteDuration(NoteValue.QUARTER),
                                NoteAttributes(null, null, null, null),
                                Pitch(NoteName.D, Octave.unsafeCreate(4))
                            ),
                            Note.Pitched(
                                NoteDuration(NoteValue._8TH),
                                NoteAttributes(null, null, null, null),
                                Pitch(NoteName.D, Octave.unsafeCreate(4))
                            ),
                            Note.Pitched(
                                NoteDuration(NoteValue._8TH),
                                NoteAttributes(null, null, null, null),
                                Pitch(NoteName.D, Octave.unsafeCreate(4))
                            ),
                            Note.Pitched(
                                NoteDuration(NoteValue.HALF),
                                NoteAttributes(null, null, null, null),
                                Pitch(NoteName.A, Octave.unsafeCreate(4))
                            )
                        )
                    ),
                    Measure(
                        null,
                        listOf(
                            Note.Pitched(
                                NoteDuration(NoteValue.QUARTER),
                                NoteAttributes(null, null, null, null),
                                Pitch(NoteName.D, Octave.unsafeCreate(4))
                            ),
                            Note.Pitched(
                                NoteDuration(NoteValue.QUARTER),
                                NoteAttributes(null, null, null, null),
                                Pitch(NoteName.D, Octave.unsafeCreate(4))
                            ),
                            Note.Pitched(
                                NoteDuration(NoteValue.QUARTER),
                                NoteAttributes(null, null, null, null),
                                Pitch(NoteName.E, Octave.unsafeCreate(4))
                            ),
                            Note.Pitched(
                                NoteDuration(NoteValue.QUARTER),
                                NoteAttributes(null, null, null, null),
                                Pitch(NoteName.E, Octave.unsafeCreate(4))
                            )
                        )
                    ),
                    Measure(
                        null,
                        listOf(
                            Note.Pitched(
                                NoteDuration(NoteValue._8TH),
                                NoteAttributes(null, null, null, null),
                                Pitch(NoteName.F, Octave.unsafeCreate(4))
                            ),
                            Note.Pitched(
                                NoteDuration(NoteValue._8TH),
                                NoteAttributes(null, null, null, null),
                                Pitch(NoteName.F, Octave.unsafeCreate(4))
                            ),
                            Note.Pitched(
                                NoteDuration(NoteValue._8TH),
                                NoteAttributes(null, null, null, null),
                                Pitch(NoteName.F, Octave.unsafeCreate(4))
                            ),
                            Note.Pitched(
                                NoteDuration(NoteValue._8TH),
                                NoteAttributes(null, null, null, null),
                                Pitch(NoteName.F, Octave.unsafeCreate(4))
                            ),
                            Note.Pitched(
                                NoteDuration(NoteValue.HALF),
                                NoteAttributes(null, null, null, null),
                                Pitch(NoteName.E, Octave.unsafeCreate(4))
                            )
                        )
                    ),
                    Measure(
                        null,
                        listOf(
                            Note.Pitched(
                                NoteDuration(NoteValue.QUARTER, NoteModifier.DOTTED),
                                NoteAttributes(null, null, null, null),
                                Pitch(NoteName.A, Octave.unsafeCreate(5))
                            ),
                            Note.Pitched(
                                NoteDuration(NoteValue._8TH),
                                NoteAttributes(null, null, null, null),
                                Pitch(NoteName.G, Octave.unsafeCreate(4))
                            ),
                            Note.Pitched(
                                NoteDuration(NoteValue.QUARTER, NoteModifier.DOTTED),
                                NoteAttributes(null, null, null, null),
                                Pitch(NoteName.A, Octave.unsafeCreate(5))
                            ),
                            Note.Pitched(
                                NoteDuration(NoteValue._8TH),
                                NoteAttributes(null, null, null, null),
                                Pitch(NoteName.G, Octave.unsafeCreate(4))
                            )
                        )
                    ),
                    Measure(
                        null,
                        listOf(
                            Note.Pitched(
                                NoteDuration(NoteValue.QUARTER, NoteModifier.DOTTED),
                                NoteAttributes(null, null, null, null),
                                Pitch(NoteName.A, Octave.unsafeCreate(5))
                            ),
                            Note.Pitched(
                                NoteDuration(NoteValue._8TH),
                                NoteAttributes(null, null, null, null),
                                Pitch(NoteName.G, Octave.unsafeCreate(4))
                            ),
                            Note.Pitched(
                                NoteDuration(NoteValue._8TH),
                                NoteAttributes(null, null, null, null),
                                Pitch(NoteName.A, Octave.unsafeCreate(5))
                            ),
                            Note.Pitched(
                                NoteDuration(NoteValue._8TH),
                                NoteAttributes(null, null, null, null),
                                Pitch(NoteName.G, Octave.unsafeCreate(4))
                            ),
                            Note.Pitched(
                                NoteDuration(NoteValue._8TH),
                                NoteAttributes(null, null, null, null),
                                Pitch(NoteName.F, Octave.unsafeCreate(4))
                            ),
                            Note.Pitched(
                                NoteDuration(NoteValue._8TH),
                                NoteAttributes(null, null, null, null),
                                Pitch(NoteName.E, Octave.unsafeCreate(4))
                            )
                        )
                    ),
                )
            )
        )

    return Score(
        ScoreId.unsafeCreate("1064db99-3726-43d7-b0ed-3fc0281bfc02"),
        ScoreTitle.unsafeCreate("Heigh Ho Nobody Home"),
        parts
    )
}
