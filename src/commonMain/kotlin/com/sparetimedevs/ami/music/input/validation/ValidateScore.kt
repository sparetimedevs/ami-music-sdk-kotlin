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
import com.sparetimedevs.ami.core.validation.ValidationError
import com.sparetimedevs.ami.core.validation.ValidationErrorForId
import com.sparetimedevs.ami.music.data.kotlin.measure.Measure
import com.sparetimedevs.ami.music.data.kotlin.measure.MeasureAttributes
import com.sparetimedevs.ami.music.data.kotlin.part.Part
import com.sparetimedevs.ami.music.data.kotlin.part.PartId
import com.sparetimedevs.ami.music.data.kotlin.score.Score
import com.sparetimedevs.ami.music.data.kotlin.score.ScoreId
import com.sparetimedevs.ami.music.data.kotlin.score.ScoreTitle

public fun com.sparetimedevs.ami.music.input.Score.validate(): EitherNel<ValidationError, Score> {
    val scoreId: Either<NonEmptyList<ValidationError>, ScoreId> =
        ScoreId.validate(this.id).toEitherNel()
    val scoreValidationErrorForId =
        ValidationErrorForId.unsafeCreate(
            scoreId.fold({ "score:no-valid-score-id" }, { "score:${it.value}" })
        )
    return Either.zipOrAccumulate(
        scoreId,
        if (this.title != null) ScoreTitle.validate(this.title).toEitherNel()
        else Either.Right(null),
        this.parts
            .map { part -> part.validate(scoreValidationErrorForId.expand("part:${part.id}")) }
            .combineAllValidationErrors()
    ) { id: ScoreId, title: ScoreTitle?, parts: List<Part> ->
        Score(id, title, parts)
    }
}

public fun com.sparetimedevs.ami.music.input.Part.validate(
    forId: ValidationErrorForId
): EitherNel<ValidationError, Part> =
    Either.zipOrAccumulate(
        PartId.validate(this.id).toEitherNel(),
        this.measures
            .withIndex()
            .map { (id, measure) -> measure.validate(forId.expand("measure:$id")) }
            .combineAllValidationErrors()
    ) { id, measures ->
        Part(id, measures)
    }

public fun com.sparetimedevs.ami.music.input.Measure.validate(
    forId: ValidationErrorForId
): EitherNel<ValidationError, Measure> =
    Either.zipOrAccumulate(
        this.attributes.validate(),
        this.notes
            .withIndex()
            .map { (id, note) -> validateNote(note, forId.expand("note:$id")) }
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
public fun <T> Iterable<Either<NonEmptyList<ValidationError>, T>>.combineAllValidationErrors(
    //    forId: ValidationErrorForId
): EitherNel<ValidationError, List<T>> =
    this.fold(emptyList<T>().right()) {
        acc: Either<NonEmptyList<ValidationError>, List<T>>,
        el: Either<NonEmptyList<ValidationError>, T> ->
        Either.zipOrAccumulate(
            { e1: NonEmptyList<ValidationError>, e2: NonEmptyList<ValidationError> ->
                e1 + e2
                //                    e2.map {
                //                        it.copy(
                //                            forId =
                //                                ValidationErrorForId.unsafeCreate(
                //                                    "ID: $errorIdPrefix" + it.forId.value
                //                                )
                //                        )
                //                    }
            },
            acc,
            el,
            { b1: List<T>, b2: T -> b1 + b2 }
        )
    }
