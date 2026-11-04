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

import arrow.core.Either
import arrow.core.EitherNel
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.nel
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.sparetimedevs.ami.core.validation.NoValidationIdentifier
import com.sparetimedevs.ami.core.validation.ValidationError
import com.sparetimedevs.ami.core.validation.ValidationIdentifier
import com.sparetimedevs.ami.core.validation.validationErrorForProperty
import com.sparetimedevs.ami.music.data.kotlin.note.NoteName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * A chord symbol as in MusicXML's `<harmony>`. [root] is `null` exactly when [kind] is [ChordKind.NONE] (N.C.).
 * [kindText] is the kind as written, e.g. `-7`.
 */
@Serializable public data class ChordSymbol(
    val root: NoteName?,
    val kind: ChordKind,
    val kindText: KindText? = null,
    val degrees: List<ChordDegree> = emptyList(),
    val bass: NoteName? = null,
) {
    public companion object {
        public fun validate(
            root: String?,
            kind: String,
            kindText: String?,
            degrees: List<ChordDegree>,
            bass: String?,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
        ): EitherNel<ValidationError, ChordSymbol> =
            Either
                .zipOrAccumulate(
                    root?.let { NoteName.validate(it, validationIdentifier) } ?: Either.Right(null),
                    ChordKind.validate(kind, validationIdentifier),
                    kindText?.let { KindText.validate(it, validationIdentifier) } ?: Either.Right(null),
                    bass?.let { NoteName.validate(it, validationIdentifier) } ?: Either.Right(null),
                ) { validRoot, validKind, validKindText, validBass ->
                    ChordSymbol(validRoot, validKind, validKindText, degrees, validBass)
                }.flatMap { symbol ->
                    // Only when everything else is valid, so the presence rules don't repeat an earlier error.
                    either { symbol.also { ensureRootAndBass(it, validationIdentifier) } }
                }
    }
}

private fun Raise<NonEmptyList<ValidationError>>.ensureRootAndBass(
    symbol: ChordSymbol,
    validationIdentifier: ValidationIdentifier,
) {
    val isNone = symbol.kind == ChordKind.NONE
    ensure(isNone == (symbol.root == null)) {
        ValidationError(
            if (isNone) "A chord of kind NONE can't have a root" else "A chord of kind ${symbol.kind} needs a root",
            validationErrorForProperty<ChordSymbol>(),
            validationIdentifier,
        ).nel()
    }
    ensure(!isNone || symbol.bass == null) {
        ValidationError(
            "A chord of kind NONE can't have a bass",
            validationErrorForProperty<ChordSymbol>(),
            validationIdentifier,
        ).nel()
    }
}

/** The chord kind as written, e.g. `m7` or `-7`. At most 32 characters. */
@Serializable
@JvmInline
public value class KindText private constructor(
    public val value: String,
) {
    public companion object {
        public const val MAX_LENGTH: Int = 32

        public fun validate(
            input: String,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
        ): EitherNel<ValidationError, KindText> =
            either {
                ensure(input.length <= MAX_LENGTH) {
                    ValidationError(
                        "Kind text can't be longer than $MAX_LENGTH characters, the input was ${input.length} characters",
                        validationErrorForProperty<KindText>(),
                        validationIdentifier,
                    ).nel()
                }
                KindText(input)
            }
    }
}
