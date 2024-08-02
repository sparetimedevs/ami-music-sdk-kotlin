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

public data class ValidationError(
    val message: String = "There was an error while validating the input.",
    // validationErrorFor is nullable because we do not always have a rich context (e.g. when
    // unsafeCreate is used).
    val validationErrorFor: ValidationErrorFor?
)

public interface ValidationErrorFor

public open class ValidationErrorForScore(public open val scoreId: String) : ValidationErrorFor {
    public open fun expandToPart(partId: String): ValidationErrorForPart =
        ValidationErrorForPart(this.scoreId, partId)

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
    public open val partId: String
) : ValidationErrorForScore(scoreId) {
    public override fun expandToPart(partId: String): ValidationErrorForPart =
        throw RuntimeException("Don't use this.")

    public open fun expandToMeasure(measureIndex: Int): ValidationErrorForMeasure =
        ValidationErrorForMeasure(this.scoreId, this.partId, measureIndex)

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
        result = 31 * result + partId.hashCode()
        return result
    }
}

public open class ValidationErrorForMeasure(
    public override val scoreId: String,
    public override val partId: String,
    public open val measureIndex: Int
) : ValidationErrorForPart(scoreId, partId) {
    public override fun expandToMeasure(measureIndex: Int): ValidationErrorForMeasure =
        throw RuntimeException("Don't use this.")

    public open fun expandToNote(noteIndex: Int): ValidationErrorForNote =
        ValidationErrorForNote(this.scoreId, this.partId, this.measureIndex, noteIndex)

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
        result = 31 * result + partId.hashCode()
        result = 31 * result + measureIndex
        return result
    }
}

public open class ValidationErrorForNote(
    public override val scoreId: String,
    public override val partId: String,
    public override val measureIndex: Int,
    public open val noteIndex: Int
) : ValidationErrorForMeasure(scoreId, partId, measureIndex) {
    public override fun expandToNote(noteIndex: Int): ValidationErrorForNote =
        throw RuntimeException("Don't use this.")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ValidationErrorForNote) return false

        if (scoreId != other.scoreId) return false
        if (partId != other.partId) return false
        if (measureIndex != other.measureIndex) return false
        if (noteIndex != other.noteIndex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = scoreId.hashCode()
        result = 31 * result + partId.hashCode()
        result = 31 * result + measureIndex
        result = 31 * result + noteIndex
        return result
    }
}
