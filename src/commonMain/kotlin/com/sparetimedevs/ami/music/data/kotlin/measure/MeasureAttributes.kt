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

package com.sparetimedevs.ami.music.data.kotlin.measure

import com.sparetimedevs.ami.music.data.kotlin.key.Key
import com.sparetimedevs.ami.music.data.kotlin.tempo.Tempo
import com.sparetimedevs.ami.music.data.kotlin.timesignature.TimeSignature
import kotlinx.serialization.Serializable

/**
 * The attributes in effect for a measure: a time signature, and a key and tempo if the part has stated one. They carry
 * forward from measure to measure, so a measure holds the values in effect for it, not only those that change there.
 * When serializing, only what differs from the previous measure is written (like MusicXML), and when deserializing,
 * what a measure doesn't state is taken from the previous one. Renderers should likewise only show what changes.
 */
@Serializable public data class MeasureAttributes(
    val timeSignature: TimeSignature,
    val key: Key? = null,
    val tempo: Tempo? = null,
)
