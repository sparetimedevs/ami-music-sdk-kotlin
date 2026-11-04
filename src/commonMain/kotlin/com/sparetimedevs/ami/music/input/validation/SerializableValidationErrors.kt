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

import com.sparetimedevs.ami.core.AccumulatedValidationErrors
import com.sparetimedevs.ami.core.Index
import com.sparetimedevs.ami.core.validation.ValidationError
import com.sparetimedevs.ami.core.validation.ValidationIdentifier
import com.sparetimedevs.ami.core.validation.ValidationLocationKind
import com.sparetimedevs.ami.music.data.kotlin.part.PartId
import com.sparetimedevs.ami.music.data.kotlin.score.ScoreId
import kotlinx.serialization.Serializable

/** One step of an error location: a [kind] with either an [id] or an [index]. */
@Serializable
public data class ValidationLocationSegment(
    val kind: ValidationLocationKind,
    val id: String? = null,
    val index: Int? = null,
) {
    /** Renders as e.g. `part p-1` or `measure 3`. */
    public fun asText(): String = "${kind.text} ${id ?: index}"
}

/** A [ValidationError] in a plain, serializable shape. The location runs from the score inward. */
@Serializable
public data class SerializableValidationError(
    val message: String,
    val property: String,
    val location: List<ValidationLocationSegment>,
) {
    /** The location as text, e.g. `part p-1, measure 3, note 2`. Empty when there is no location. */
    public fun locationAsText(): String = location.joinToString(", ") { it.asText() }
}

/** [AccumulatedValidationErrors] in a plain, serializable shape. */
@Serializable
public data class SerializableValidationErrors(
    val message: String,
    val errors: List<SerializableValidationError>,
)

/** The location segments of this identifier, from the score inward. [NoValidationIdentifier] is left out. */
public fun ValidationIdentifier.locationSegments(): List<ValidationLocationSegment> {
    val segments = mutableListOf<ValidationLocationSegment>()
    var current: ValidationIdentifier = this
    while (true) {
        val kind = current.kind ?: break
        val identifier = current.identifier
        segments +=
            when (identifier) {
                is Index -> ValidationLocationSegment(kind, index = identifier.value)
                is ScoreId -> ValidationLocationSegment(kind, id = identifier.value)
                is PartId -> ValidationLocationSegment(kind, id = identifier.value)
                else -> ValidationLocationSegment(kind, id = identifier.toString())
            }
        val parent = current.validationIdentifierParent
        if (parent === current) break
        current = parent
    }
    return segments.reversed()
}

/** The location of this identifier as text, e.g. `part p-1, measure 3, note 2`. */
public fun ValidationIdentifier.locationAsText(): String = locationSegments().joinToString(", ") { it.asText() }

public fun ValidationError.toSerializable(): SerializableValidationError =
    SerializableValidationError(
        message = message,
        property = validationErrorForProperty.value,
        location = validationIdentifier.locationSegments(),
    )

public fun AccumulatedValidationErrors.toSerializable(): SerializableValidationErrors =
    SerializableValidationErrors(
        message = message,
        errors = validationErrors.map { it.toSerializable() },
    )
