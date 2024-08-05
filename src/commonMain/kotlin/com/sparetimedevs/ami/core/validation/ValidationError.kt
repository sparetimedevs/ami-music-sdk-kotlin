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

package com.sparetimedevs.ami.core.validation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.sparetimedevs.ami.music.data.kotlin.part.PartId
import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

public data class ValidationError(
    val message: String = "There was an error while validating the input.",
    // validationErrorFor is nullable because we do not always have a rich context (e.g. when
    // unsafeCreate is used).
    // TODO can become not nullable if we use special type. Not sure what the advantages are.
    val validationErrorFor: ValidationErrorFor?,
    val validationIdentifier: ValidationIdentifier
)

// TODO try out this cool alternative
public interface ValidationIdentifier {
    public val validationIdentifierParent: ValidationIdentifier
}

public object NoValidationIdentifier : ValidationIdentifier {
    public override val validationIdentifierParent: ValidationIdentifier = this
}

public data class ValidationIdentifierForScore(
    public val scoreId: String,
    public override val validationIdentifierParent: ValidationIdentifier
) : ValidationIdentifier

public data class ValidationIdentifierForPart(
    public val partId: String,
    public override val validationIdentifierParent: ValidationIdentifier
) : ValidationIdentifier

public data class ValidationIdentifierForMeasure(
    public val measureIndex: Int,
    public override val validationIdentifierParent: ValidationIdentifier
) : ValidationIdentifier

public data class ValidationIdentifierForNote(
    public val noteIndex: Int,
    public override val validationIdentifierParent: ValidationIdentifier
) : ValidationIdentifier

public interface ValidationErrorFor

public object ValidationErrorForUnknown : ValidationErrorFor

public open class ValidationErrorForScore(public open val scoreId: String) : ValidationErrorFor {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ValidationErrorForScore) return false

        if (scoreId != other.scoreId) return false

        return true
    }

    override fun hashCode(): Int {
        return scoreId.hashCode()
    }
}

public open class ValidationErrorForPart(
    public override val scoreId: String,
    public open val partId: PartId?
) : ValidationErrorForScore(scoreId) {

    public companion object {
        public fun fromParent(parent: ValidationErrorFor, partId: String): ValidationErrorForPart =
            when (parent) {
                is ValidationErrorForScore ->
                    ValidationErrorForPart(
                        parent.scoreId,
                        PartId.validate(partId, ValidationErrorForUnknown, NoValidationIdentifier)
                            .getOrNull()
                    )
                else ->
                    ValidationErrorForPart(
                        "todo",
                        PartId.validate(partId, ValidationErrorForUnknown, NoValidationIdentifier)
                            .getOrNull()
                    )
            }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ValidationErrorForPart) return false
        if (!super.equals(other)) return false

        if (scoreId != other.scoreId) return false
        if (partId != other.partId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + scoreId.hashCode()
        result = 31 * result + (partId?.hashCode() ?: 0)
        return result
    }
}

public open class ValidationErrorForMeasure(
    public override val scoreId: String,
    public override val partId: PartId?,
    public open val measureIndex: MeasureIndex?
) : ValidationErrorForPart(scoreId, partId) {

    public companion object {
        public fun fromParent(
            parent: ValidationErrorFor,
            measureIndex: Int
        ): ValidationErrorForMeasure =
            when (parent) {
                is ValidationErrorForPart ->
                    ValidationErrorForMeasure(
                        parent.scoreId,
                        parent.partId,
                        MeasureIndex.getValidOrNull(measureIndex)
                    )
                is ValidationErrorForScore ->
                    ValidationErrorForMeasure(
                        parent.scoreId,
                        null,
                        MeasureIndex.getValidOrNull(measureIndex)
                    )
                else ->
                    ValidationErrorForMeasure(
                        "todo",
                        null,
                        MeasureIndex.getValidOrNull(measureIndex)
                    )
            }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ValidationErrorForMeasure) return false
        if (!super.equals(other)) return false

        if (scoreId != other.scoreId) return false
        if (partId != other.partId) return false
        if (measureIndex != other.measureIndex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + scoreId.hashCode()
        result = 31 * result + (partId?.hashCode() ?: 0)
        result = 31 * result + (measureIndex?.hashCode() ?: 0)
        return result
    }
}

@Serializable
@JvmInline
public value class MeasureIndex private constructor(public val value: Int) {
    public companion object {
        public fun validate(
            input: Int,
            validationErrorFor: ValidationErrorFor = ValidationErrorForUnknown,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier
        ): Either<ValidationError, MeasureIndex> = either {
            ensure(input >= 0) {
                ValidationError(
                    "MeasureIndex can't be negative, the input was $input",
                    validationErrorFor,
                    validationIdentifier
                )
            }
            MeasureIndex(input)
        }

        public fun getValidOrNull(input: Int): MeasureIndex? =
            validate(input, ValidationErrorForUnknown, NoValidationIdentifier).getOrNull()

        public fun unsafeCreate(input: Int): MeasureIndex =
            validate(input, ValidationErrorForUnknown, NoValidationIdentifier).getOrThrow()
    }
}

public open class ValidationErrorForNote(
    public override val scoreId: String,
    public override val partId: PartId?,
    public override val measureIndex: MeasureIndex?,
    public open val noteIndex: NoteIndex?
) : ValidationErrorForMeasure(scoreId, partId, measureIndex) {

    public companion object {
        public fun fromParent(parent: ValidationErrorFor, noteIndex: Int): ValidationErrorForNote =
            when (parent) {
                is ValidationErrorForMeasure ->
                    ValidationErrorForNote(
                        parent.scoreId,
                        parent.partId,
                        parent.measureIndex,
                        NoteIndex.getValidOrNull(noteIndex)
                    )
                is ValidationErrorForPart ->
                    ValidationErrorForNote(
                        parent.scoreId,
                        parent.partId,
                        null,
                        NoteIndex.getValidOrNull(noteIndex)
                    )
                is ValidationErrorForScore ->
                    ValidationErrorForNote(
                        parent.scoreId,
                        null,
                        null,
                        NoteIndex.getValidOrNull(noteIndex)
                    )
                else ->
                    ValidationErrorForNote("todo", null, null, NoteIndex.getValidOrNull(noteIndex))
            }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ValidationErrorForNote) return false
        if (!super.equals(other)) return false

        if (scoreId != other.scoreId) return false
        if (partId != other.partId) return false
        if (measureIndex != other.measureIndex) return false
        if (noteIndex != other.noteIndex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + scoreId.hashCode()
        result = 31 * result + (partId?.hashCode() ?: 0)
        result = 31 * result + (measureIndex?.hashCode() ?: 0)
        result = 31 * result + (noteIndex?.hashCode() ?: 0)
        return result
    }
}

@Serializable
@JvmInline
public value class NoteIndex private constructor(public val value: Int) {
    public companion object {
        public fun validate(
            input: Int,
            validationErrorFor: ValidationErrorFor = ValidationErrorForUnknown,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier
        ): Either<ValidationError, NoteIndex> = either {
            ensure(input >= 0) {
                ValidationError(
                    "NoteIndex can't be negative, the input was $input",
                    validationErrorFor,
                    validationIdentifier
                )
            }
            NoteIndex(input)
        }

        public fun getValidOrNull(input: Int): NoteIndex? =
            validate(input, ValidationErrorForUnknown, NoValidationIdentifier).getOrNull()

        public fun unsafeCreate(input: Int): NoteIndex =
            validate(input, ValidationErrorForUnknown, NoValidationIdentifier).getOrThrow()
    }
}
