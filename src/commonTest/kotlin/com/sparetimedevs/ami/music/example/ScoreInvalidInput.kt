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

import com.sparetimedevs.ami.music.input.Measure
import com.sparetimedevs.ami.music.input.NoteAttributes
import com.sparetimedevs.ami.music.input.NoteDuration
import com.sparetimedevs.ami.music.input.Part
import com.sparetimedevs.ami.music.input.Pitch
import com.sparetimedevs.ami.music.input.Pitched
import com.sparetimedevs.ami.music.input.Score

fun getExampleScore0InvalidInput(): Score {
    val parts =
        listOf(
            Part(
                id = "p-1",
                measures =
                    listOf(
                        Measure(
                            null,
                            listOf(
                                Pitched(
                                    duration = NoteDuration("QUARTAAAR", "NONE"),
                                    noteAttributes =
                                        NoteAttributes(
                                            attack = null,
                                            dynamics = null,
                                            endDynamics = null,
                                            release = null
                                        ),
                                    pitch = Pitch(alter = 0.0f, noteName = "C", octave = 4)
                                ),
                                Pitched(
                                    duration = NoteDuration("QUARTER", "NONE"),
                                    noteAttributes =
                                        NoteAttributes(
                                            attack = null,
                                            dynamics = null,
                                            endDynamics = null,
                                            release = null
                                        ),
                                    pitch = Pitch(alter = 0.0f, noteName = "G", octave = 127)
                                ),
                                Pitched(
                                    duration = NoteDuration("QUARTER", "NONE"),
                                    noteAttributes =
                                        NoteAttributes(
                                            attack = null,
                                            dynamics = null,
                                            endDynamics = null,
                                            release = null
                                        ),
                                    pitch = Pitch(alter = 0.0f, noteName = "A", octave = 4)
                                ),
                                Pitched(
                                    duration = NoteDuration("QUARTER", "NONE"),
                                    noteAttributes =
                                        NoteAttributes(
                                            attack = null,
                                            dynamics = null,
                                            endDynamics = null,
                                            release = null
                                        ),
                                    pitch = Pitch(alter = 0.0f, noteName = "L", octave = 4)
                                )
                            )
                        ),
                        Measure(
                            attributes = null,
                            notes =
                                listOf(
                                    Pitched(
                                        duration = NoteDuration("QUARTER", "NONE"),
                                        noteAttributes =
                                            NoteAttributes(
                                                attack = null,
                                                dynamics = null,
                                                endDynamics = null,
                                                release = null
                                            ),
                                        pitch = Pitch(alter = 0.0f, noteName = "C", octave = 4)
                                    ),
                                    Pitched(
                                        duration = NoteDuration("QUARTER", "NONE"),
                                        noteAttributes =
                                            NoteAttributes(
                                                attack = null,
                                                dynamics = null,
                                                endDynamics = null,
                                                release = null
                                            ),
                                        pitch = Pitch(alter = 0.0f, noteName = "C", octave = 4)
                                    ),
                                    Pitched(
                                        duration = NoteDuration("QUARTER", "NONE"),
                                        noteAttributes =
                                            NoteAttributes(
                                                attack = null,
                                                dynamics = null,
                                                endDynamics = null,
                                                release = null
                                            ),
                                        pitch = Pitch(alter = 0.0f, noteName = "A", octave = 4)
                                    ),
                                    Pitched(
                                        duration = NoteDuration("QUARTER", "NONE"),
                                        noteAttributes =
                                            NoteAttributes(
                                                attack = null,
                                                dynamics = null,
                                                endDynamics = null,
                                                release = null
                                            ),
                                        pitch = Pitch(alter = 0.0f, noteName = "A", octave = 4)
                                    )
                                )
                        ),
                    )
            ),
            Part(
                id = "p-2",
                measures =
                    listOf(
                        Measure(
                            attributes = null,
                            notes =
                                listOf(
                                    Pitched(
                                        duration = NoteDuration("QUARTER", "NONE"),
                                        noteAttributes =
                                            NoteAttributes(
                                                attack = null,
                                                dynamics = null,
                                                endDynamics = null,
                                                release = null
                                            ),
                                        pitch = Pitch(alter = 0.0f, noteName = "C", octave = 4)
                                    ),
                                    Pitched(
                                        duration = NoteDuration("QUARTER", "NONE"),
                                        noteAttributes =
                                            NoteAttributes(
                                                attack = null,
                                                dynamics = null,
                                                endDynamics = null,
                                                release = null
                                            ),
                                        pitch = Pitch(alter = 0.0f, noteName = "G", octave = 4)
                                    ),
                                    Pitched(
                                        duration = NoteDuration("QUARTER", "NONE"),
                                        noteAttributes =
                                            NoteAttributes(
                                                attack = null,
                                                dynamics = null,
                                                endDynamics = null,
                                                release = null
                                            ),
                                        pitch = Pitch(alter = 0.0f, noteName = "A", octave = 4)
                                    ),
                                    Pitched(
                                        duration = NoteDuration("QUARTER", "NONE"),
                                        noteAttributes =
                                            NoteAttributes(
                                                attack = null,
                                                dynamics = null,
                                                endDynamics = null,
                                                release = null
                                            ),
                                        pitch = Pitch(alter = 0.0f, noteName = "E", octave = 4)
                                    )
                                )
                        ),
                        Measure(
                            attributes = null,
                            notes =
                                listOf(
                                    Pitched(
                                        duration = NoteDuration("QUARTER", "NONE"),
                                        noteAttributes =
                                            NoteAttributes(
                                                attack = null,
                                                dynamics = null,
                                                endDynamics = null,
                                                release = null
                                            ),
                                        pitch = Pitch(alter = 0.0f, noteName = "C", octave = 4)
                                    ),
                                    Pitched(
                                        duration = NoteDuration("QUARTER", "NONE"),
                                        noteAttributes =
                                            NoteAttributes(
                                                attack = null,
                                                dynamics = null,
                                                endDynamics = null,
                                                release = null
                                            ),
                                        pitch = Pitch(alter = 0.0f, noteName = "C", octave = 4)
                                    ),
                                    Pitched(
                                        duration = NoteDuration("QUARTER", "NONE"),
                                        noteAttributes =
                                            NoteAttributes(
                                                attack = null,
                                                dynamics = null,
                                                endDynamics = null,
                                                release = null
                                            ),
                                        pitch = Pitch(alter = 0.0f, noteName = "A", octave = 4)
                                    ),
                                    Pitched(
                                        duration = NoteDuration("QUARTER", "NONE"),
                                        noteAttributes =
                                            NoteAttributes(
                                                attack = null,
                                                dynamics = null,
                                                endDynamics = null,
                                                release = null
                                            ),
                                        pitch = Pitch(alter = 0.0f, noteName = "A", octave = 4)
                                    )
                                )
                        )
                    )
            )
        )

    return Score("d737b4ae-fbaa-4b0d-9d36-d3651e30e93a", null, parts)
}
