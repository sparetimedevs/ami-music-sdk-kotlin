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

import com.sparetimedevs.ami.core.validation.ValidationError
import com.sparetimedevs.ami.core.validation.ValidationErrorForId
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec

class OctaveTest :
    StringSpec({
        val forId = ValidationErrorForId.unsafeCreate("test")
        "validate should return valid octave" {
            val input: Byte = 4
            Octave.validate(input, forId) shouldBeRight Octave.unsafeCreate(4)
        }
        "validate should return invalid with error" {
            val input: Byte = -13
            Octave.validate(input, forId) shouldBeLeft
                ValidationError("Octave can't be lesser than -12, the input was -13", forId)
        }
    })
