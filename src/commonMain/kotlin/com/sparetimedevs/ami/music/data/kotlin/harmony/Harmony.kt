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

package com.sparetimedevs.ami.music.data.kotlin.harmony

import com.sparetimedevs.ami.music.data.kotlin.chord.ChordSymbol
import com.sparetimedevs.ami.music.data.kotlin.fraction.Fraction
import kotlinx.serialization.Serializable

/** A chord symbol at a position in a measure. [offset] is a fraction of a whole note from the start of the measure. */
@Serializable
public data class Harmony(
    val offset: Fraction,
    val chord: ChordSymbol,
)
