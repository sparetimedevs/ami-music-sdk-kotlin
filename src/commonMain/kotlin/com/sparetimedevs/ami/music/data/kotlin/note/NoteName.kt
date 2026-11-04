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
public enum class NoteName {
    A_FLAT,
    A,
    A_SHARP,
    B_FLAT,
    B,
    B_SHARP,
    C_FLAT,
    C,
    C_SHARP,
    D_FLAT,
    D,
    D_SHARP,
    E_FLAT,
    E,
    E_SHARP,
    F_FLAT,
    F,
    F_SHARP,
    G_FLAT,
    G,
    G_SHARP,
    ;

    public companion object {
        public fun validate(
            input: String,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
        ): EitherNel<ValidationError, NoteName> =
            when (val noteName = entries.firstOrNull { it.name == input }) {
                null ->
                    ValidationError(
                        "Note name can't be value $input",
                        validationErrorForProperty<NoteName>(),
                        validationIdentifier,
                    ).nel()
                        .left()

                else -> noteName.right()
            }
    }
}
