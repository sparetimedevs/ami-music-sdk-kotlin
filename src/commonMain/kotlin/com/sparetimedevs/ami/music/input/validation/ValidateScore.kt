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
import arrow.core.flatMap
import com.sparetimedevs.ami.core.validation.NoValidationIdentifier
import com.sparetimedevs.ami.core.validation.ValidationError
import com.sparetimedevs.ami.core.validation.ValidationIdentifier
import com.sparetimedevs.ami.core.validation.combineAllValidationErrors
import com.sparetimedevs.ami.music.data.kotlin.measure.Measure
import com.sparetimedevs.ami.music.data.kotlin.measure.MeasureAttributes
import com.sparetimedevs.ami.music.data.kotlin.part.Part
import com.sparetimedevs.ami.music.data.kotlin.part.PartId
import com.sparetimedevs.ami.music.data.kotlin.part.PartName
import com.sparetimedevs.ami.music.data.kotlin.score.Score
import com.sparetimedevs.ami.music.data.kotlin.score.ScoreId
import com.sparetimedevs.ami.music.data.kotlin.score.ScoreTitle

public fun com.sparetimedevs.ami.music.input.Score.validate(
    validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
): EitherNel<ValidationError, Score> =
    ScoreId.validate(this.id, validationIdentifier).flatMap { scoreId ->
        val validationIdentifierForScore =
            ValidationIdentifierForScore(scoreId, validationIdentifier)

        Either.zipOrAccumulate(
            if (this.title != null) {
                ScoreTitle.validate(this.title, validationIdentifierForScore)
            } else {
                Either.Right(null)
            },
            this.parts
                .map { part -> part.validate(validationIdentifierForScore) }
                .combineAllValidationErrors(),
        ) { title, parts ->
            Score(scoreId, title, parts)
        }
    }

public fun com.sparetimedevs.ami.music.input.Part.validate(
    validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
): EitherNel<ValidationError, Part> =
    PartId.validate(this.id, validationIdentifier).flatMap { partId ->
        val validationIdentifierForPart = ValidationIdentifierForPart(partId, validationIdentifier)

        Either.zipOrAccumulate(
            PartName.validate(this.name, validationIdentifierForPart),
            this.instrument.validate(validationIdentifierForPart),
            this.measures
                .withIndex()
                .map { (index, measure) -> measure.validate(index, validationIdentifierForPart) }
                .combineAllValidationErrors(),
        ) { name, instrument, measures ->
            Part(partId, name, instrument, measures)
        }
    }

public fun com.sparetimedevs.ami.music.input.Measure.validate(
    index: Int,
    validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
): EitherNel<ValidationError, Measure> =
    ValidationIdentifierForMeasure(index, validationIdentifier).let { validationIdentifierForMeasure,
        ->
        Either.zipOrAccumulate(
            this.attributes.validate(validationIdentifierForMeasure),
            this.notes
                .withIndex()
                .map { (index, note) -> validateNote(index, note, validationIdentifierForMeasure) }
                .combineAllValidationErrors(),
        ) { attributes, notes ->
            Measure(attributes, notes)
        }
    }

public fun com.sparetimedevs.ami.music.input.MeasureAttributes?.validate(
    validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
): EitherNel<ValidationError, MeasureAttributes?> {
    // TODO use, and validate "this" provided attributes.
    return Either.Right(null)
}
