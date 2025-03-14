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

package com.sparetimedevs.ami.music.data.kotlin.part

import arrow.core.nel
import com.sparetimedevs.ami.core.validation.NoValidationIdentifier
import com.sparetimedevs.ami.core.validation.ValidationError
import com.sparetimedevs.ami.core.validation.validationErrorForProperty
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import kotlin.test.Test

class PartInstrumentNameTest {
    @Test
    fun `validate should work with valid input`() {
        val input = "Grand Piano"
        PartInstrumentName.validate(input) shouldBeRight PartInstrumentName.unsafeCreate(input)
    }

    @Test
    fun `validate should work with null as input`() {
        val input = null
        PartInstrumentName.validate(input) shouldBeRight null
    }

    @Test
    fun `validate should work with empty string as input`() {
        val input = ""
        PartInstrumentName.validate(input) shouldBeRight null
    }

    @Test
    fun `validate should yield validation error for invalid value`() {
        val input =
            """Some very lengthy input string. With a lot of nonsense and repetition.
                |Some very lengthy input string. With a lot of nonsense and repetition.
                |Some very lengthy input string. With a lot of nonsense and repetition.
                |Some very lengthy input string. With a lot of nonsense and repetition.
                |Some very lengthy input string. With a lot of nonsense and repetition.
                |Some very lengthy input string. With a lot of nonsense and repetition.
                |Some very lengthy input string. With a lot of nonsense and repetition.
                |Some very lengthy input string. With a lot of nonsense and repetition.
                |Some very lengthy input string. With a lot of nonsense and repetition.
                |Some very lengthy input string. With a lot of nonsense and repetition.
                |Some very lengthy input string. With a lot of nonsense and repetition.
                |Some very lengthy input string. With a lot of nonsense and repetition.
                |Some very lengthy input string. With a lot of nonsense and repetition.
                |Some very lengthy input string. With a lot of nonsense and repetition.
                |Some very lengthy input string. With a lot of nonsense and repetition.
                |Some very lengthy input string. With a lot of nonsense and repetition.
                |Some very lengthy input string. With a lot of nonsense and repetition.
                |Some very lengthy input string. With a lot of nonsense and repetition.
                |Some very lengthy input string. With a lot of nonsense and repetition.
                |Some very lengthy input string. With a lot of nonsense and repetition.
                |Some very lengthy input string. With a lot of nonsense and repetition.
                |Some very lengthy input string. With a lot of nonsense and repetition.
                |Some very lengthy input string. With a lot of nonsense and repetition.
                |Some very lengthy input string. With a lot of nonsense and repetition.
                |Some very lengthy input string. With a lot of nonsense and repetition.
                |Some very lengthy input string. With a lot of nonsense and repetition.
                |Some very lengthy input string. With a lot of nonsense and repetition.
                |Some very lengthy input string. With a lot of nonsense and repetition.
                |Some very lengthy input string. With a lot of nonsense and repetition.
                |Some very lengthy input string. With a lot of nonsense and repetition.
            """.trimMargin()
        PartInstrumentName.validate(input) shouldBeLeft
            ValidationError(
                "Part instrument name can't be longer than 512 characters, the input was $input",
                validationErrorForProperty<PartInstrumentName>(),
                NoValidationIdentifier,
            ).nel()
    }
}
