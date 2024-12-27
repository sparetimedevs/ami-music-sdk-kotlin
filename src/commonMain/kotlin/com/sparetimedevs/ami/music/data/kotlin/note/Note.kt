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

package com.sparetimedevs.ami.music.data.kotlin.note

import kotlinx.serialization.Serializable

@Serializable
public sealed interface Note {
    public val duration: NoteDuration
    public val noteAttributes: NoteAttributes

    @Serializable
    public data class Pitched(
        override val duration: NoteDuration,
        override val noteAttributes: NoteAttributes,
        val pitch: Pitch,
    ) : Note

    @Serializable
    public data class Chord(
        override val duration: NoteDuration,
        override val noteAttributes: NoteAttributes,
        val rootNote: Pitch,
        val pitches: List<Pitch>,
    ) : Note

    @Serializable
    public data class Rest(
        override val duration: NoteDuration,
        override val noteAttributes: NoteAttributes,
    ) : Note

    @Serializable
    public data class Unpitched(
        override val duration: NoteDuration,
        override val noteAttributes: NoteAttributes,
    ) : Note
}
