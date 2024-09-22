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

import arrow.core.Either
import arrow.core.left
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
    C,
    C_SHARP,
    D_FLAT,
    D,
    D_SHARP,
    E_FLAT,
    E,
    F,
    F_SHARP,
    G_FLAT,
    G,
    G_SHARP;

    public companion object {

        public fun validate(
            input: String,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier
        ): Either<ValidationError, NoteName> =
            when (input) {
                A_FLAT.name -> A_FLAT.right()
                A.name -> A.right()
                A_SHARP.name -> A_SHARP.right()
                B_FLAT.name -> B_FLAT.right()
                B.name -> B.right()
                C.name -> C.right()
                C_SHARP.name -> C_SHARP.right()
                D_FLAT.name -> D_FLAT.right()
                D.name -> D.right()
                D_SHARP.name -> D_SHARP.right()
                E_FLAT.name -> E_FLAT.right()
                E.name -> E.right()
                F.name -> F.right()
                F_SHARP.name -> F_SHARP.right()
                G_FLAT.name -> G_FLAT.right()
                G.name -> G.right()
                G_SHARP.name -> G_SHARP.right()
                else ->
                    ValidationError(
                            "Note name can't be value $input",
                            validationErrorForProperty<NoteName>(),
                            validationIdentifier
                        )
                        .left()
            }
    }
}
