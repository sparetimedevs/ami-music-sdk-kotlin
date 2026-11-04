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

package com.sparetimedevs.ami.music.input.validation

import arrow.core.Either
import arrow.core.EitherNel
import arrow.core.flatMap
import arrow.core.leftNel
import com.sparetimedevs.ami.core.validation.NoValidationIdentifier
import com.sparetimedevs.ami.core.validation.ValidationError
import com.sparetimedevs.ami.core.validation.ValidationErrorForProperty
import com.sparetimedevs.ami.core.validation.ValidationIdentifier
import com.sparetimedevs.ami.core.validation.combineAllValidationErrors
import com.sparetimedevs.ami.core.validation.validationErrorForProperty
import com.sparetimedevs.ami.music.data.kotlin.measure.Measure
import com.sparetimedevs.ami.music.data.kotlin.measure.MeasureAttributes
import com.sparetimedevs.ami.music.data.kotlin.part.Part
import com.sparetimedevs.ami.music.data.kotlin.part.PartId
import com.sparetimedevs.ami.music.data.kotlin.part.PartName
import com.sparetimedevs.ami.music.data.kotlin.score.Score
import com.sparetimedevs.ami.music.data.kotlin.score.ScoreId
import com.sparetimedevs.ami.music.data.kotlin.score.ScoreTitle
import com.sparetimedevs.ami.music.data.kotlin.timesignature.BeatType
import com.sparetimedevs.ami.music.data.kotlin.timesignature.Beats
import com.sparetimedevs.ami.music.data.kotlin.timesignature.TimeSignature

public fun com.sparetimedevs.ami.music.serialization.Score.validate(
    validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
): EitherNel<ValidationError, Score> =
    ScoreId.validate(this.id, validationIdentifier).flatMap { scoreId ->
        val validationIdentifierForScore =
            ValidationIdentifierForScore(scoreId, validationIdentifier)

        Either.zipOrAccumulate(
            ScoreTitle.validate(this.title, validationIdentifierForScore),
            this.parts
                .map { part -> part.validate(validationIdentifierForScore) }
                .combineAllValidationErrors(),
        ) { title, parts ->
            Score(scoreId, title, parts)
        }
    }

public fun com.sparetimedevs.ami.music.serialization.Part.validate(
    validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
): EitherNel<ValidationError, Part> =
    PartId.validate(this.id, validationIdentifier).flatMap { partId ->
        val validationIdentifierForPart = ValidationIdentifierForPart(partId, validationIdentifier)

        Either.zipOrAccumulate(
            PartName.validate(this.name, validationIdentifierForPart),
            this.instrument.validate(validationIdentifierForPart),
            validateMeasures(this.measures, validationIdentifierForPart),
        ) { name, instrument, measures ->
            Part(partId, name, instrument, measures)
        }
    }

/**
 * A measure depends on the ones before it (the time signature carries forward, D4), so first the attributes and notes of
 * all measures are validated, and then the measures are built in order.
 */
private fun validateMeasures(
    measures: List<com.sparetimedevs.ami.music.serialization.Measure>,
    validationIdentifier: ValidationIdentifier,
): EitherNel<ValidationError, List<Measure>> =
    Either.zipOrAccumulate(
        measures
            .withIndex()
            .map { (index, measure) ->
                measure.attributes.validate(ValidationIdentifierForMeasure(index, validationIdentifier))
            }.combineAllValidationErrors(),
        measures
            .withIndex()
            .map { (index, measure) ->
                val validationIdentifierForMeasure = ValidationIdentifierForMeasure(index, validationIdentifier)
                measure.notes
                    .withIndex()
                    .map { (noteIndex, note) -> validateNote(noteIndex, note, validationIdentifierForMeasure) }
                    .combineAllValidationErrors()
            }.combineAllValidationErrors(),
    ) { attributes, notes ->
        var previousTimeSignature: TimeSignature? = null
        attributes.zip(notes).map { (measureAttributes, measureNotes) ->
            Measure(measureAttributes, measureNotes, previousTimeSignature).also {
                previousTimeSignature = it.timeSignature
            }
        }
    }

/** Empty attributes (`"attributes": {}`) are the same as no attributes. */
public fun com.sparetimedevs.ami.music.serialization.MeasureAttributes?.validate(
    validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
): EitherNel<ValidationError, MeasureAttributes?> =
    if (this == null) {
        Either.Right(null)
    } else {
        // TODO validate key.
        this.time.validate(validationIdentifier).map { time ->
            MeasureAttributes(key = null, time = time).takeUnless { it.isEmpty() }
        }
    }

public fun com.sparetimedevs.ami.music.serialization.TimeSignature?.validate(
    validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
): EitherNel<ValidationError, TimeSignature?> =
    if (this == null) {
        Either.Right(null)
    } else {
        Either.zipOrAccumulate(
            validateBeats(this.beats, validationIdentifier),
            validateBeatType(this.beatType, validationIdentifier),
        ) { beats, beatType -> TimeSignature(beats, beatType) }
    }

// An int32 outside the Byte range is a validation error, not a wrapped value.
private fun validateBeats(
    input: Int,
    validationIdentifier: ValidationIdentifier,
): EitherNel<ValidationError, Beats> =
    if (input in Byte.MIN_VALUE..Byte.MAX_VALUE) {
        Beats.validate(input.toByte(), validationIdentifier)
    } else {
        outsideByteRange("Beats", input, validationErrorForProperty<Beats>(), validationIdentifier).leftNel()
    }

private fun validateBeatType(
    input: Int,
    validationIdentifier: ValidationIdentifier,
): EitherNel<ValidationError, BeatType> =
    if (input in Byte.MIN_VALUE..Byte.MAX_VALUE) {
        BeatType.validate(input.toByte(), validationIdentifier)
    } else {
        outsideByteRange("BeatType", input, validationErrorForProperty<BeatType>(), validationIdentifier).leftNel()
    }

private fun outsideByteRange(
    name: String,
    input: Int,
    validationErrorForProperty: ValidationErrorForProperty,
    validationIdentifier: ValidationIdentifier,
): ValidationError =
    ValidationError(
        "$name can't be bigger than ${Byte.MAX_VALUE} or smaller than ${Byte.MIN_VALUE}, the input was $input",
        validationErrorForProperty,
        validationIdentifier,
    )
