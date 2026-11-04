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

package com.sparetimedevs.ami.music.data.kotlin.chord

import arrow.core.EitherNel
import arrow.core.left
import arrow.core.nel
import arrow.core.right
import com.sparetimedevs.ami.core.validation.NoValidationIdentifier
import com.sparetimedevs.ami.core.validation.ValidationError
import com.sparetimedevs.ami.core.validation.ValidationIdentifier
import com.sparetimedevs.ami.core.validation.validationErrorForProperty
import kotlinx.serialization.Serializable

/** The 33 chord kinds of MusicXML. [musicXmlName] is the kind-value as MusicXML spells it. */
@Serializable
public enum class ChordKind(
    public val musicXmlName: String,
) {
    MAJOR("major"),
    MINOR("minor"),
    AUGMENTED("augmented"),
    DIMINISHED("diminished"),
    DOMINANT("dominant"),
    MAJOR_SEVENTH("major-seventh"),
    MINOR_SEVENTH("minor-seventh"),
    DIMINISHED_SEVENTH("diminished-seventh"),
    AUGMENTED_SEVENTH("augmented-seventh"),
    HALF_DIMINISHED("half-diminished"),
    MAJOR_MINOR("major-minor"),
    MAJOR_SIXTH("major-sixth"),
    MINOR_SIXTH("minor-sixth"),
    DOMINANT_NINTH("dominant-ninth"),
    MAJOR_NINTH("major-ninth"),
    MINOR_NINTH("minor-ninth"),
    DOMINANT_11TH("dominant-11th"),
    MAJOR_11TH("major-11th"),
    MINOR_11TH("minor-11th"),
    DOMINANT_13TH("dominant-13th"),
    MAJOR_13TH("major-13th"),
    MINOR_13TH("minor-13th"),
    SUSPENDED_SECOND("suspended-second"),
    SUSPENDED_FOURTH("suspended-fourth"),
    NEAPOLITAN("Neapolitan"),
    ITALIAN("Italian"),
    FRENCH("French"),
    GERMAN("German"),
    PEDAL("pedal"),
    POWER("power"),
    TRISTAN("Tristan"),
    OTHER("other"),
    NONE("none"),
    ;

    public companion object {
        /** [input] is the entry name, e.g. `MINOR_SEVENTH`, not the MusicXML name. */
        public fun validate(
            input: String,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
        ): EitherNel<ValidationError, ChordKind> =
            when (val kind = entries.firstOrNull { it.name == input }) {
                null ->
                    ValidationError(
                        "Chord kind can't be value $input",
                        validationErrorForProperty<ChordKind>(),
                        validationIdentifier,
                    ).nel()
                        .left()

                else -> kind.right()
            }
    }
}
