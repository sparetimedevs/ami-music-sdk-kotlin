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

package com.sparetimedevs.ami.music.input.validation

import arrow.core.Either
import arrow.core.EitherNel
import arrow.core.right
import com.sparetimedevs.ami.core.validation.NoValidationIdentifier
import com.sparetimedevs.ami.core.validation.ValidationError
import com.sparetimedevs.ami.core.validation.ValidationIdentifier
import com.sparetimedevs.ami.music.data.kotlin.midi.MidiChannel
import com.sparetimedevs.ami.music.data.kotlin.midi.MidiProgram
import com.sparetimedevs.ami.music.data.kotlin.part.PartInstrument
import com.sparetimedevs.ami.music.data.kotlin.part.PartInstrumentName

public fun com.sparetimedevs.ami.music.input.PartInstrument?.validate(
    validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
): EitherNel<ValidationError, PartInstrument?> =
    if (this == null) {
        null.right()
    } else {
        Either.zipOrAccumulate(
            PartInstrumentName.validate(this.name, validationIdentifier),
            MidiChannel.validate(this.midiChannel, validationIdentifier),
            MidiProgram.validate(this.midiProgram, validationIdentifier),
        ) { name, midiChannel, midiProgram ->
            PartInstrument(name, midiChannel, midiProgram)
        }
    }
