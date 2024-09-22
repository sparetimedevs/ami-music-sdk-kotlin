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
import arrow.core.NonEmptyList
import arrow.core.right
import arrow.core.toEitherNel
import com.sparetimedevs.ami.core.validation.NoValidationIdentifier
import com.sparetimedevs.ami.core.validation.ValidationError
import com.sparetimedevs.ami.core.validation.ValidationIdentifier
import com.sparetimedevs.ami.core.validation.ValidationIdentifierForMeasure
import com.sparetimedevs.ami.core.validation.ValidationIdentifierForNote
import com.sparetimedevs.ami.core.validation.ValidationIdentifierForPart
import com.sparetimedevs.ami.core.validation.ValidationIdentifierForScore
import com.sparetimedevs.ami.music.data.kotlin.measure.Measure
import com.sparetimedevs.ami.music.data.kotlin.measure.MeasureAttributes
import com.sparetimedevs.ami.music.data.kotlin.part.Part
import com.sparetimedevs.ami.music.data.kotlin.part.PartId
import com.sparetimedevs.ami.music.data.kotlin.part.PartName
import com.sparetimedevs.ami.music.data.kotlin.score.Score
import com.sparetimedevs.ami.music.data.kotlin.score.ScoreId
import com.sparetimedevs.ami.music.data.kotlin.score.ScoreTitle

public fun com.sparetimedevs.ami.music.input.Score.validate(
    validationIdentifier: ValidationIdentifier = NoValidationIdentifier
): EitherNel<ValidationError, Score> {
    val scoreId: Either<NonEmptyList<ValidationError>, ScoreId> =
        ScoreId.validate(this.id).toEitherNel()
    val validationIdentifierForScore =
        ValidationIdentifierForScore(
            scoreId.fold({ "no-valid-score-id" }, { it.value }),
            validationIdentifier
        )
    return Either.zipOrAccumulate(
        scoreId,
        if (this.title != null)
            ScoreTitle.validate(this.title, validationIdentifierForScore).toEitherNel()
        else Either.Right(null),
        this.parts
            .map { part ->
                part.validate(ValidationIdentifierForPart(part.id, validationIdentifierForScore))
            }
            .combineAllValidationErrors()
    ) { id: ScoreId, title: ScoreTitle?, parts: List<Part> ->
        Score(id, title, parts)
    }
}

public fun com.sparetimedevs.ami.music.input.Part.validate(
    validationIdentifier: ValidationIdentifier = NoValidationIdentifier
): EitherNel<ValidationError, Part> =
    Either.zipOrAccumulate(
        PartId.validate(this.id, validationIdentifier).toEitherNel(),
        PartName.validate(this.name, validationIdentifier).toEitherNel(),
        this.instrument.validate(validationIdentifier),
        this.measures
            .withIndex()
            .map { (index, measure) ->
                measure.validate(ValidationIdentifierForMeasure(index, validationIdentifier))
            }
            .combineAllValidationErrors()
    ) { id, name, instrument, measures ->
        Part(id, name, instrument, measures)
    }

public tailrec fun getListOfValidationIdentifiers(
    validationIdentifier: ValidationIdentifier,
    acc: List<ValidationIdentifier> = emptyList()
): List<ValidationIdentifier> =
    if (validationIdentifier == NoValidationIdentifier) acc
    else {
        getListOfValidationIdentifiers(
            validationIdentifier.validationIdentifierParent,
            acc + validationIdentifier
        )
    }

public tailrec fun returnFirstScoreValidationIdentifier(
    validationIdentifier: ValidationIdentifier
): ValidationIdentifierForScore =
    if (validationIdentifier is ValidationIdentifierForScore) validationIdentifier
    else {
        returnFirstScoreValidationIdentifier(validationIdentifier.validationIdentifierParent)
    }

public fun com.sparetimedevs.ami.music.input.Measure.validate(
    validationIdentifier: ValidationIdentifier = NoValidationIdentifier
): EitherNel<ValidationError, Measure> =
    Either.zipOrAccumulate(
        this.attributes.validate(),
        this.notes
            .withIndex()
            .map { (index, note) ->
                validateNote(note, ValidationIdentifierForNote(index, validationIdentifier))
            }
            .combineAllValidationErrors()
    ) { attributes, notes ->
        Measure(attributes, notes)
    }

public fun com.sparetimedevs.ami.music.input.MeasureAttributes?.validate():
    EitherNel<ValidationError, MeasureAttributes?> {
    // TODO use, and validate "this" provided attributes.
    return Either.Right(null)
}

// TODO move somewhere where it makes sense.
public fun <T> Iterable<Either<NonEmptyList<ValidationError>, T>>.combineAllValidationErrors():
    EitherNel<ValidationError, List<T>> =
    this.fold(emptyList<T>().right()) {
        acc: Either<NonEmptyList<ValidationError>, List<T>>,
        el: Either<NonEmptyList<ValidationError>, T> ->
        Either.zipOrAccumulate(
            { e1: NonEmptyList<ValidationError>, e2: NonEmptyList<ValidationError> -> e1 + e2 },
            acc,
            el,
            { b1: List<T>, b2: T -> b1 + b2 }
        )
    }
