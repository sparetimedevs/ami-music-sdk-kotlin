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
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.leftNel
import com.sparetimedevs.ami.core.validation.NoValidationIdentifier
import com.sparetimedevs.ami.core.validation.ValidationError
import com.sparetimedevs.ami.core.validation.ValidationErrorForProperty
import com.sparetimedevs.ami.core.validation.ValidationIdentifier
import com.sparetimedevs.ami.core.validation.combineAllValidationErrors
import com.sparetimedevs.ami.core.validation.validationErrorForProperty
import com.sparetimedevs.ami.music.data.kotlin.fraction.Fraction
import com.sparetimedevs.ami.music.data.kotlin.key.Fifths
import com.sparetimedevs.ami.music.data.kotlin.key.Key
import com.sparetimedevs.ami.music.data.kotlin.key.KeyMode
import com.sparetimedevs.ami.music.data.kotlin.measure.Measure
import com.sparetimedevs.ami.music.data.kotlin.measure.MeasureAttributes
import com.sparetimedevs.ami.music.data.kotlin.part.Part
import com.sparetimedevs.ami.music.data.kotlin.part.PartId
import com.sparetimedevs.ami.music.data.kotlin.part.PartName
import com.sparetimedevs.ami.music.data.kotlin.score.Score
import com.sparetimedevs.ami.music.data.kotlin.score.ScoreId
import com.sparetimedevs.ami.music.data.kotlin.score.ScoreTitle
import com.sparetimedevs.ami.music.data.kotlin.tempo.QuarterNotesPerMinute
import com.sparetimedevs.ami.music.data.kotlin.tempo.Tempo
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
 * A measure depends on the ones before it (the time signature carries forward, D4, and a pickup is checked against the
 * time signature), so first the attributes, notes and pickups of all measures are validated, and then the measures are
 * built in order.
 */
private fun validateMeasures(
    measures: List<com.sparetimedevs.ami.music.serialization.Measure>,
    validationIdentifier: ValidationIdentifier,
): EitherNel<ValidationError, List<Measure>> =
    Either
        .zipOrAccumulate(
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
            measures
                .withIndex()
                .map { (index, measure) ->
                    measure.pickup.validatePickup(ValidationIdentifierForMeasure(index, validationIdentifier))
                }.combineAllValidationErrors(),
        ) { attributes, notes, pickups -> Triple(attributes, notes, pickups) }
        .flatMap { (attributes, notes, pickups) ->
            var previousTimeSignature: TimeSignature? = null
            val built =
                attributes.indices.map { index ->
                    Measure(attributes[index], notes[index], previousTimeSignature, pickups[index]).also {
                        previousTimeSignature = it.timeSignature
                    }
                }
            built
                .withIndex()
                .mapNotNull { (index, measure) ->
                    measure.pickup?.let {
                        checkPickup(
                            index,
                            it,
                            measure.timeSignature,
                            ValidationIdentifierForMeasure(index, validationIdentifier),
                        )
                    }
                }.let { errors ->
                    if (errors.isEmpty()) {
                        Either.Right(
                            built,
                        )
                    } else {
                        Either.Left(NonEmptyList(errors.first(), errors.drop(1)))
                    }
                }
        }

/** D5, §4.3: a pickup is allowed only on a part's first measure, and `0 < pickup < timeSignature.measureLength`. */
private fun checkPickup(
    index: Int,
    pickup: Fraction,
    timeSignature: TimeSignature,
    validationIdentifier: ValidationIdentifier,
): ValidationError? =
    when {
        index != 0 ->
            ValidationError(
                "Pickup $pickup is only allowed on the first measure of a part",
                ValidationErrorForProperty(PICKUP_PROPERTY),
                validationIdentifier,
            )
        pickup <= Fraction.ZERO || pickup >= timeSignature.measureLength ->
            ValidationError(
                "Pickup $pickup must be bigger than 0 and smaller than the measure length " +
                    "${timeSignature.measureLength}",
                ValidationErrorForProperty(PICKUP_PROPERTY),
                validationIdentifier,
            )
        else -> null
    }

private const val PICKUP_PROPERTY = "Pickup"

private fun com.sparetimedevs.ami.music.serialization.Fraction?.validatePickup(
    validationIdentifier: ValidationIdentifier,
): EitherNel<ValidationError, Fraction?> = if (this == null) Either.Right(null) else validate(validationIdentifier)

/** Empty attributes (`"attributes": {}`) are the same as no attributes. */
public fun com.sparetimedevs.ami.music.serialization.MeasureAttributes?.validate(
    validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
): EitherNel<ValidationError, MeasureAttributes?> =
    if (this == null) {
        Either.Right(null)
    } else {
        Either.zipOrAccumulate(
            this.key.validate(validationIdentifier),
            this.time.validate(validationIdentifier),
            this.tempo.validate(validationIdentifier),
        ) { key, time, tempo ->
            MeasureAttributes(key = key, time = time, tempo = tempo).takeUnless { it.isEmpty() }
        }
    }

public fun com.sparetimedevs.ami.music.serialization.Key?.validate(
    validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
): EitherNel<ValidationError, Key?> =
    if (this == null) {
        Either.Right(null)
    } else {
        Either.zipOrAccumulate(
            Fifths.validate(this.fifths, validationIdentifier),
            KeyMode.validate(this.mode, validationIdentifier),
        ) { fifths, mode -> Key(fifths, mode) }
    }

public fun com.sparetimedevs.ami.music.serialization.Fraction.validate(
    validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
): EitherNel<ValidationError, Fraction> = Fraction.validate(this.numerator, this.denominator, validationIdentifier)

public fun com.sparetimedevs.ami.music.serialization.Tempo?.validate(
    validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
): EitherNel<ValidationError, Tempo?> =
    if (this == null) {
        Either.Right(null)
    } else {
        QuarterNotesPerMinute.validate(this.quarterNotesPerMinute, validationIdentifier).map { Tempo(it) }
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
