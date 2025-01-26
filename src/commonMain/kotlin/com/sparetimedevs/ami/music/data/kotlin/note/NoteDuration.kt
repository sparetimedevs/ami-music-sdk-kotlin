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

import arrow.core.EitherNel
import arrow.core.left
import arrow.core.nel
import arrow.core.right
import com.sparetimedevs.ami.core.validation.NoValidationIdentifier
import com.sparetimedevs.ami.core.validation.ValidationError
import com.sparetimedevs.ami.core.validation.ValidationIdentifier
import com.sparetimedevs.ami.core.validation.validationErrorForProperty
import kotlinx.serialization.Serializable

@Serializable
public data class NoteDuration(
    val noteValue: NoteValue,
    val modifier: NoteModifier = NoteModifier.NONE,
) {
    val value: Double = noteValue.value * modifier.value

    public companion object {
        public fun validate(
            input: Double,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
        ): EitherNel<ValidationError, NoteDuration> =
            when (input) {
                NoteValue.MAXIMA.value * NoteModifier.DOTTED.value ->
                    NoteDuration(NoteValue.MAXIMA, NoteModifier.DOTTED).right()
                NoteValue.MAXIMA.value * NoteModifier.DOUBLE_DOTTED.value ->
                    NoteDuration(NoteValue.MAXIMA, NoteModifier.DOUBLE_DOTTED).right()
                NoteValue.MAXIMA.value * NoteModifier.TRIPLE_DOTTED.value ->
                    NoteDuration(NoteValue.MAXIMA, NoteModifier.TRIPLE_DOTTED).right()
                NoteValue.MAXIMA.value * NoteModifier.QUADRUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.MAXIMA, NoteModifier.QUADRUPLE_DOTTED).right()
                NoteValue.MAXIMA.value * NoteModifier.QUINTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.MAXIMA, NoteModifier.QUINTUPLE_DOTTED).right()
                NoteValue.MAXIMA.value * NoteModifier.SEXTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.MAXIMA, NoteModifier.SEXTUPLE_DOTTED).right()
                NoteValue.MAXIMA.value * NoteModifier.SEPTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.MAXIMA, NoteModifier.SEPTUPLE_DOTTED).right()
                NoteValue.MAXIMA.value * NoteModifier.OCTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.MAXIMA, NoteModifier.OCTUPLE_DOTTED).right()
                NoteValue.MAXIMA.value -> NoteDuration(NoteValue.MAXIMA).right()
                NoteValue.LONG.value * NoteModifier.DOTTED.value ->
                    NoteDuration(NoteValue.LONG, NoteModifier.DOTTED).right()
                NoteValue.LONG.value * NoteModifier.DOUBLE_DOTTED.value ->
                    NoteDuration(NoteValue.LONG, NoteModifier.DOUBLE_DOTTED).right()
                NoteValue.LONG.value * NoteModifier.TRIPLE_DOTTED.value ->
                    NoteDuration(NoteValue.LONG, NoteModifier.TRIPLE_DOTTED).right()
                NoteValue.LONG.value * NoteModifier.QUADRUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.LONG, NoteModifier.QUADRUPLE_DOTTED).right()
                NoteValue.LONG.value * NoteModifier.QUINTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.LONG, NoteModifier.QUINTUPLE_DOTTED).right()
                NoteValue.LONG.value * NoteModifier.SEXTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.LONG, NoteModifier.SEXTUPLE_DOTTED).right()
                NoteValue.LONG.value * NoteModifier.SEPTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.LONG, NoteModifier.SEPTUPLE_DOTTED).right()
                NoteValue.LONG.value * NoteModifier.OCTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.LONG, NoteModifier.OCTUPLE_DOTTED).right()
                NoteValue.LONG.value -> NoteDuration(NoteValue.LONG).right()
                NoteValue.DOUBLE_WHOLE.value * NoteModifier.DOTTED.value ->
                    NoteDuration(NoteValue.DOUBLE_WHOLE, NoteModifier.DOTTED).right()
                NoteValue.DOUBLE_WHOLE.value * NoteModifier.DOUBLE_DOTTED.value ->
                    NoteDuration(NoteValue.DOUBLE_WHOLE, NoteModifier.DOUBLE_DOTTED).right()
                NoteValue.DOUBLE_WHOLE.value * NoteModifier.TRIPLE_DOTTED.value ->
                    NoteDuration(NoteValue.DOUBLE_WHOLE, NoteModifier.TRIPLE_DOTTED).right()
                NoteValue.DOUBLE_WHOLE.value * NoteModifier.QUADRUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.DOUBLE_WHOLE, NoteModifier.QUADRUPLE_DOTTED).right()
                NoteValue.DOUBLE_WHOLE.value * NoteModifier.QUINTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.DOUBLE_WHOLE, NoteModifier.QUINTUPLE_DOTTED).right()
                NoteValue.DOUBLE_WHOLE.value * NoteModifier.SEXTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.DOUBLE_WHOLE, NoteModifier.SEXTUPLE_DOTTED).right()
                NoteValue.DOUBLE_WHOLE.value * NoteModifier.SEPTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.DOUBLE_WHOLE, NoteModifier.SEPTUPLE_DOTTED).right()
                NoteValue.DOUBLE_WHOLE.value * NoteModifier.OCTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.DOUBLE_WHOLE, NoteModifier.OCTUPLE_DOTTED).right()
                NoteValue.DOUBLE_WHOLE.value -> NoteDuration(NoteValue.DOUBLE_WHOLE).right()
                NoteValue.WHOLE.value * NoteModifier.DOTTED.value ->
                    NoteDuration(NoteValue.WHOLE, NoteModifier.DOTTED).right()
                NoteValue.WHOLE.value * NoteModifier.DOUBLE_DOTTED.value ->
                    NoteDuration(NoteValue.WHOLE, NoteModifier.DOUBLE_DOTTED).right()
                NoteValue.WHOLE.value * NoteModifier.TRIPLE_DOTTED.value ->
                    NoteDuration(NoteValue.WHOLE, NoteModifier.TRIPLE_DOTTED).right()
                NoteValue.WHOLE.value * NoteModifier.QUADRUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.WHOLE, NoteModifier.QUADRUPLE_DOTTED).right()
                NoteValue.WHOLE.value * NoteModifier.QUINTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.WHOLE, NoteModifier.QUINTUPLE_DOTTED).right()
                NoteValue.WHOLE.value * NoteModifier.SEXTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.WHOLE, NoteModifier.SEXTUPLE_DOTTED).right()
                NoteValue.WHOLE.value * NoteModifier.SEPTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.WHOLE, NoteModifier.SEPTUPLE_DOTTED).right()
                NoteValue.WHOLE.value * NoteModifier.OCTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.WHOLE, NoteModifier.OCTUPLE_DOTTED).right()
                NoteValue.WHOLE.value -> NoteDuration(NoteValue.WHOLE).right()
                NoteValue.HALF.value * NoteModifier.DOTTED.value ->
                    NoteDuration(NoteValue.HALF, NoteModifier.DOTTED).right()
                NoteValue.HALF.value * NoteModifier.DOUBLE_DOTTED.value ->
                    NoteDuration(NoteValue.HALF, NoteModifier.DOUBLE_DOTTED).right()
                NoteValue.HALF.value * NoteModifier.TRIPLE_DOTTED.value ->
                    NoteDuration(NoteValue.HALF, NoteModifier.TRIPLE_DOTTED).right()
                NoteValue.HALF.value * NoteModifier.QUADRUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.HALF, NoteModifier.QUADRUPLE_DOTTED).right()
                NoteValue.HALF.value * NoteModifier.QUINTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.HALF, NoteModifier.QUINTUPLE_DOTTED).right()
                NoteValue.HALF.value * NoteModifier.SEXTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.HALF, NoteModifier.SEXTUPLE_DOTTED).right()
                NoteValue.HALF.value * NoteModifier.SEPTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.HALF, NoteModifier.SEPTUPLE_DOTTED).right()
                NoteValue.HALF.value * NoteModifier.OCTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.HALF, NoteModifier.OCTUPLE_DOTTED).right()
                NoteValue.HALF.value -> NoteDuration(NoteValue.HALF).right()
                NoteValue.QUARTER.value * NoteModifier.DOTTED.value ->
                    NoteDuration(NoteValue.QUARTER, NoteModifier.DOTTED).right()
                NoteValue.QUARTER.value * NoteModifier.DOUBLE_DOTTED.value ->
                    NoteDuration(NoteValue.QUARTER, NoteModifier.DOUBLE_DOTTED).right()
                NoteValue.QUARTER.value * NoteModifier.TRIPLE_DOTTED.value ->
                    NoteDuration(NoteValue.QUARTER, NoteModifier.TRIPLE_DOTTED).right()
                NoteValue.QUARTER.value * NoteModifier.QUADRUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.QUARTER, NoteModifier.QUADRUPLE_DOTTED).right()
                NoteValue.QUARTER.value * NoteModifier.QUINTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.QUARTER, NoteModifier.QUINTUPLE_DOTTED).right()
                NoteValue.QUARTER.value * NoteModifier.SEXTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.QUARTER, NoteModifier.SEXTUPLE_DOTTED).right()
                NoteValue.QUARTER.value * NoteModifier.SEPTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.QUARTER, NoteModifier.SEPTUPLE_DOTTED).right()
                NoteValue.QUARTER.value * NoteModifier.OCTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue.QUARTER, NoteModifier.OCTUPLE_DOTTED).right()
                NoteValue.QUARTER.value -> NoteDuration(NoteValue.QUARTER).right()
                NoteValue._8TH.value * NoteModifier.DOTTED.value ->
                    NoteDuration(NoteValue._8TH, NoteModifier.DOTTED).right()
                NoteValue._8TH.value * NoteModifier.DOUBLE_DOTTED.value ->
                    NoteDuration(NoteValue._8TH, NoteModifier.DOUBLE_DOTTED).right()
                NoteValue._8TH.value * NoteModifier.TRIPLE_DOTTED.value ->
                    NoteDuration(NoteValue._8TH, NoteModifier.TRIPLE_DOTTED).right()
                NoteValue._8TH.value * NoteModifier.QUADRUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._8TH, NoteModifier.QUADRUPLE_DOTTED).right()
                NoteValue._8TH.value * NoteModifier.QUINTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._8TH, NoteModifier.QUINTUPLE_DOTTED).right()
                NoteValue._8TH.value * NoteModifier.SEXTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._8TH, NoteModifier.SEXTUPLE_DOTTED).right()
                NoteValue._8TH.value * NoteModifier.SEPTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._8TH, NoteModifier.SEPTUPLE_DOTTED).right()
                NoteValue._8TH.value * NoteModifier.OCTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._8TH, NoteModifier.OCTUPLE_DOTTED).right()
                NoteValue._8TH.value -> NoteDuration(NoteValue._8TH).right()
                NoteValue._16TH.value * NoteModifier.DOTTED.value ->
                    NoteDuration(NoteValue._16TH, NoteModifier.DOTTED).right()
                NoteValue._16TH.value * NoteModifier.DOUBLE_DOTTED.value ->
                    NoteDuration(NoteValue._16TH, NoteModifier.DOUBLE_DOTTED).right()
                NoteValue._16TH.value * NoteModifier.TRIPLE_DOTTED.value ->
                    NoteDuration(NoteValue._16TH, NoteModifier.TRIPLE_DOTTED).right()
                NoteValue._16TH.value * NoteModifier.QUADRUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._16TH, NoteModifier.QUADRUPLE_DOTTED).right()
                NoteValue._16TH.value * NoteModifier.QUINTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._16TH, NoteModifier.QUINTUPLE_DOTTED).right()
                NoteValue._16TH.value * NoteModifier.SEXTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._16TH, NoteModifier.SEXTUPLE_DOTTED).right()
                NoteValue._16TH.value * NoteModifier.SEPTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._16TH, NoteModifier.SEPTUPLE_DOTTED).right()
                NoteValue._16TH.value * NoteModifier.OCTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._16TH, NoteModifier.OCTUPLE_DOTTED).right()
                NoteValue._16TH.value -> NoteDuration(NoteValue._16TH).right()
                NoteValue._32ND.value * NoteModifier.DOTTED.value ->
                    NoteDuration(NoteValue._32ND, NoteModifier.DOTTED).right()
                NoteValue._32ND.value * NoteModifier.DOUBLE_DOTTED.value ->
                    NoteDuration(NoteValue._32ND, NoteModifier.DOUBLE_DOTTED).right()
                NoteValue._32ND.value * NoteModifier.TRIPLE_DOTTED.value ->
                    NoteDuration(NoteValue._32ND, NoteModifier.TRIPLE_DOTTED).right()
                NoteValue._32ND.value * NoteModifier.QUADRUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._32ND, NoteModifier.QUADRUPLE_DOTTED).right()
                NoteValue._32ND.value * NoteModifier.QUINTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._32ND, NoteModifier.QUINTUPLE_DOTTED).right()
                NoteValue._32ND.value * NoteModifier.SEXTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._32ND, NoteModifier.SEXTUPLE_DOTTED).right()
                NoteValue._32ND.value * NoteModifier.SEPTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._32ND, NoteModifier.SEPTUPLE_DOTTED).right()
                NoteValue._32ND.value * NoteModifier.OCTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._32ND, NoteModifier.OCTUPLE_DOTTED).right()
                NoteValue._32ND.value -> NoteDuration(NoteValue._32ND).right()
                NoteValue._64TH.value * NoteModifier.DOTTED.value ->
                    NoteDuration(NoteValue._64TH, NoteModifier.DOTTED).right()
                NoteValue._64TH.value * NoteModifier.DOUBLE_DOTTED.value ->
                    NoteDuration(NoteValue._64TH, NoteModifier.DOUBLE_DOTTED).right()
                NoteValue._64TH.value * NoteModifier.TRIPLE_DOTTED.value ->
                    NoteDuration(NoteValue._64TH, NoteModifier.TRIPLE_DOTTED).right()
                NoteValue._64TH.value * NoteModifier.QUADRUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._64TH, NoteModifier.QUADRUPLE_DOTTED).right()
                NoteValue._64TH.value * NoteModifier.QUINTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._64TH, NoteModifier.QUINTUPLE_DOTTED).right()
                NoteValue._64TH.value * NoteModifier.SEXTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._64TH, NoteModifier.SEXTUPLE_DOTTED).right()
                NoteValue._64TH.value * NoteModifier.SEPTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._64TH, NoteModifier.SEPTUPLE_DOTTED).right()
                NoteValue._64TH.value * NoteModifier.OCTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._64TH, NoteModifier.OCTUPLE_DOTTED).right()
                NoteValue._64TH.value -> NoteDuration(NoteValue._64TH).right()
                NoteValue._128TH.value * NoteModifier.DOTTED.value ->
                    NoteDuration(NoteValue._128TH, NoteModifier.DOTTED).right()
                NoteValue._128TH.value * NoteModifier.DOUBLE_DOTTED.value ->
                    NoteDuration(NoteValue._128TH, NoteModifier.DOUBLE_DOTTED).right()
                NoteValue._128TH.value * NoteModifier.TRIPLE_DOTTED.value ->
                    NoteDuration(NoteValue._128TH, NoteModifier.TRIPLE_DOTTED).right()
                NoteValue._128TH.value * NoteModifier.QUADRUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._128TH, NoteModifier.QUADRUPLE_DOTTED).right()
                NoteValue._128TH.value * NoteModifier.QUINTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._128TH, NoteModifier.QUINTUPLE_DOTTED).right()
                NoteValue._128TH.value * NoteModifier.SEXTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._128TH, NoteModifier.SEXTUPLE_DOTTED).right()
                NoteValue._128TH.value * NoteModifier.SEPTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._128TH, NoteModifier.SEPTUPLE_DOTTED).right()
                NoteValue._128TH.value * NoteModifier.OCTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._128TH, NoteModifier.OCTUPLE_DOTTED).right()
                NoteValue._128TH.value -> NoteDuration(NoteValue._128TH).right()
                NoteValue._256TH.value * NoteModifier.DOTTED.value ->
                    NoteDuration(NoteValue._256TH, NoteModifier.DOTTED).right()
                NoteValue._256TH.value * NoteModifier.DOUBLE_DOTTED.value ->
                    NoteDuration(NoteValue._256TH, NoteModifier.DOUBLE_DOTTED).right()
                NoteValue._256TH.value * NoteModifier.TRIPLE_DOTTED.value ->
                    NoteDuration(NoteValue._256TH, NoteModifier.TRIPLE_DOTTED).right()
                NoteValue._256TH.value * NoteModifier.QUADRUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._256TH, NoteModifier.QUADRUPLE_DOTTED).right()
                NoteValue._256TH.value * NoteModifier.QUINTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._256TH, NoteModifier.QUINTUPLE_DOTTED).right()
                NoteValue._256TH.value * NoteModifier.SEXTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._256TH, NoteModifier.SEXTUPLE_DOTTED).right()
                NoteValue._256TH.value * NoteModifier.SEPTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._256TH, NoteModifier.SEPTUPLE_DOTTED).right()
                NoteValue._256TH.value * NoteModifier.OCTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._256TH, NoteModifier.OCTUPLE_DOTTED).right()
                NoteValue._256TH.value -> NoteDuration(NoteValue._256TH).right()
                NoteValue._512TH.value * NoteModifier.DOTTED.value ->
                    NoteDuration(NoteValue._512TH, NoteModifier.DOTTED).right()
                NoteValue._512TH.value * NoteModifier.DOUBLE_DOTTED.value ->
                    NoteDuration(NoteValue._512TH, NoteModifier.DOUBLE_DOTTED).right()
                NoteValue._512TH.value * NoteModifier.TRIPLE_DOTTED.value ->
                    NoteDuration(NoteValue._512TH, NoteModifier.TRIPLE_DOTTED).right()
                NoteValue._512TH.value * NoteModifier.QUADRUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._512TH, NoteModifier.QUADRUPLE_DOTTED).right()
                NoteValue._512TH.value * NoteModifier.QUINTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._512TH, NoteModifier.QUINTUPLE_DOTTED).right()
                NoteValue._512TH.value * NoteModifier.SEXTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._512TH, NoteModifier.SEXTUPLE_DOTTED).right()
                NoteValue._512TH.value * NoteModifier.SEPTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._512TH, NoteModifier.SEPTUPLE_DOTTED).right()
                NoteValue._512TH.value * NoteModifier.OCTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._512TH, NoteModifier.OCTUPLE_DOTTED).right()
                NoteValue._512TH.value -> NoteDuration(NoteValue._512TH).right()
                NoteValue._1024TH.value * NoteModifier.DOTTED.value ->
                    NoteDuration(NoteValue._1024TH, NoteModifier.DOTTED).right()
                NoteValue._1024TH.value * NoteModifier.DOUBLE_DOTTED.value ->
                    NoteDuration(NoteValue._1024TH, NoteModifier.DOUBLE_DOTTED).right()
                NoteValue._1024TH.value * NoteModifier.TRIPLE_DOTTED.value ->
                    NoteDuration(NoteValue._1024TH, NoteModifier.TRIPLE_DOTTED).right()
                NoteValue._1024TH.value * NoteModifier.QUADRUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._1024TH, NoteModifier.QUADRUPLE_DOTTED).right()
                NoteValue._1024TH.value * NoteModifier.QUINTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._1024TH, NoteModifier.QUINTUPLE_DOTTED).right()
                NoteValue._1024TH.value * NoteModifier.SEXTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._1024TH, NoteModifier.SEXTUPLE_DOTTED).right()
                NoteValue._1024TH.value * NoteModifier.SEPTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._1024TH, NoteModifier.SEPTUPLE_DOTTED).right()
                NoteValue._1024TH.value * NoteModifier.OCTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._1024TH, NoteModifier.OCTUPLE_DOTTED).right()
                NoteValue._1024TH.value -> NoteDuration(NoteValue._1024TH).right()
                NoteValue._2048TH.value * NoteModifier.DOTTED.value ->
                    NoteDuration(NoteValue._2048TH, NoteModifier.DOTTED).right()
                NoteValue._2048TH.value * NoteModifier.DOUBLE_DOTTED.value ->
                    NoteDuration(NoteValue._2048TH, NoteModifier.DOUBLE_DOTTED).right()
                NoteValue._2048TH.value * NoteModifier.TRIPLE_DOTTED.value ->
                    NoteDuration(NoteValue._2048TH, NoteModifier.TRIPLE_DOTTED).right()
                NoteValue._2048TH.value * NoteModifier.QUADRUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._2048TH, NoteModifier.QUADRUPLE_DOTTED).right()
                NoteValue._2048TH.value * NoteModifier.QUINTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._2048TH, NoteModifier.QUINTUPLE_DOTTED).right()
                NoteValue._2048TH.value * NoteModifier.SEXTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._2048TH, NoteModifier.SEXTUPLE_DOTTED).right()
                NoteValue._2048TH.value * NoteModifier.SEPTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._2048TH, NoteModifier.SEPTUPLE_DOTTED).right()
                NoteValue._2048TH.value * NoteModifier.OCTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._2048TH, NoteModifier.OCTUPLE_DOTTED).right()
                NoteValue._2048TH.value -> NoteDuration(NoteValue._2048TH).right()
                NoteValue._4096TH.value * NoteModifier.DOTTED.value ->
                    NoteDuration(NoteValue._4096TH, NoteModifier.DOTTED).right()
                NoteValue._4096TH.value * NoteModifier.DOUBLE_DOTTED.value ->
                    NoteDuration(NoteValue._4096TH, NoteModifier.DOUBLE_DOTTED).right()
                NoteValue._4096TH.value * NoteModifier.TRIPLE_DOTTED.value ->
                    NoteDuration(NoteValue._4096TH, NoteModifier.TRIPLE_DOTTED).right()
                NoteValue._4096TH.value * NoteModifier.QUADRUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._4096TH, NoteModifier.QUADRUPLE_DOTTED).right()
                NoteValue._4096TH.value * NoteModifier.QUINTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._4096TH, NoteModifier.QUINTUPLE_DOTTED).right()
                NoteValue._4096TH.value * NoteModifier.SEXTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._4096TH, NoteModifier.SEXTUPLE_DOTTED).right()
                NoteValue._4096TH.value * NoteModifier.SEPTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._4096TH, NoteModifier.SEPTUPLE_DOTTED).right()
                NoteValue._4096TH.value * NoteModifier.OCTUPLE_DOTTED.value ->
                    NoteDuration(NoteValue._4096TH, NoteModifier.OCTUPLE_DOTTED).right()
                NoteValue._4096TH.value -> NoteDuration(NoteValue._4096TH).right()
                else ->
                    ValidationError(
                        "Input for note duration is not a valid value, the value is: $input",
                        validationErrorForProperty<NoteDuration>(),
                        validationIdentifier,
                    ).nel()
                        .left()
            }
    }
}

@Suppress("ktlint:standard:enum-entry-name-case")
@Serializable
public enum class NoteValue(
    public val value: Double,
) {
    MAXIMA(8.0),
    LONG(4.0),
    DOUBLE_WHOLE(2.0),
    WHOLE(1.0),
    HALF(0.5),
    QUARTER(0.25),
    _8TH(0.125),
    _16TH(0.0625),
    _32ND(0.03125),
    _64TH(0.015625),
    _128TH(0.0078125),
    _256TH(0.00390625),
    _512TH(0.001953125),
    _1024TH(0.0009765625),
    _2048TH(0.00048828125),
    _4096TH(0.000244140625),
    ;

    public companion object {
        public fun validate(
            input: String,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
        ): EitherNel<ValidationError, NoteValue> =
            when (input) {
                MAXIMA.name -> MAXIMA.right()
                LONG.name -> LONG.right()
                DOUBLE_WHOLE.name -> DOUBLE_WHOLE.right()
                WHOLE.name -> WHOLE.right()
                HALF.name -> HALF.right()
                QUARTER.name -> QUARTER.right()
                _8TH.name -> _8TH.right()
                _16TH.name -> _16TH.right()
                _32ND.name -> _32ND.right()
                _64TH.name -> _64TH.right()
                _128TH.name -> _128TH.right()
                _256TH.name -> _256TH.right()
                _512TH.name -> _512TH.right()
                _1024TH.name -> _1024TH.right()
                _2048TH.name -> _2048TH.right()
                _4096TH.name -> _4096TH.right()
                else ->
                    ValidationError(
                        "Note value can't be value $input",
                        validationErrorForProperty<NoteDuration>(),
                        validationIdentifier,
                    ).nel()
                        .left()
            }
    }
}

@Serializable
public enum class NoteModifier(
    public val value: Double,
) {
    NONE(1.0),
    DOTTED(1.5),
    DOUBLE_DOTTED(1.25),
    TRIPLE_DOTTED(1.125),
    QUADRUPLE_DOTTED(1.0625),
    QUINTUPLE_DOTTED(1.03125),
    SEXTUPLE_DOTTED(1.015625),
    SEPTUPLE_DOTTED(1.0078125),
    OCTUPLE_DOTTED(1.00390625),
    ;

    public companion object {
        public fun validate(
            input: String,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
        ): EitherNel<ValidationError, NoteModifier> =
            when (input) {
                NONE.name -> NONE.right()
                DOTTED.name -> DOTTED.right()
                DOUBLE_DOTTED.name -> DOUBLE_DOTTED.right()
                TRIPLE_DOTTED.name -> TRIPLE_DOTTED.right()
                QUADRUPLE_DOTTED.name -> QUADRUPLE_DOTTED.right()
                QUINTUPLE_DOTTED.name -> QUINTUPLE_DOTTED.right()
                SEXTUPLE_DOTTED.name -> SEXTUPLE_DOTTED.right()
                SEPTUPLE_DOTTED.name -> SEPTUPLE_DOTTED.right()
                OCTUPLE_DOTTED.name -> OCTUPLE_DOTTED.right()
                else ->
                    ValidationError(
                        "Note modifier can't be value $input",
                        validationErrorForProperty<NoteDuration>(),
                        validationIdentifier,
                    ).nel()
                        .left()
            }
    }
}
