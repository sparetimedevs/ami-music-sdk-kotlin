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

package com.sparetimedevs.ami.music.example

import com.sparetimedevs.ami.music.data.kotlin.measure.Measure
import com.sparetimedevs.ami.music.data.kotlin.measure.MeasureAttributes
import com.sparetimedevs.ami.music.data.kotlin.note.Note
import com.sparetimedevs.ami.music.data.kotlin.timesignature.BeatType
import com.sparetimedevs.ami.music.data.kotlin.timesignature.Beats
import com.sparetimedevs.ami.music.data.kotlin.timesignature.TimeSignature

internal val FOUR_FOUR: TimeSignature = TimeSignature(Beats.unsafeCreate(4), BeatType.unsafeCreate(4))

internal val ATTRIBUTES_IN_FOUR_FOUR: MeasureAttributes = MeasureAttributes(timeSignature = FOUR_FOUR)

/** Creates a measure in 4/4. */
internal fun createMeasure(vararg notes: Note): Measure =
    Measure(attributes = ATTRIBUTES_IN_FOUR_FOUR, notes = notes.asList())
