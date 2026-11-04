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

package com.sparetimedevs.ami.core.validation

import com.sparetimedevs.ami.core.Id
import com.sparetimedevs.ami.core.IdOrIndex
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
public data class ValidationError(
    val message: String = "There was an error while validating the input.",
    val validationErrorForProperty: ValidationErrorForProperty,
    val validationIdentifier: ValidationIdentifier,
)

@Serializable @JvmInline
public value class ValidationErrorForProperty(
    public val value: String,
)

public inline fun <reified T : Any> validationErrorForProperty(): ValidationErrorForProperty =
    ValidationErrorForProperty(T::class.simpleName ?: "unknown")

/** The kind of thing a [ValidationIdentifier] points at. [text] is the name used in JSON and in rendered locations. */
@Serializable
public enum class ValidationLocationKind(
    public val text: String,
) {
    @SerialName("score")
    SCORE("score"),

    @SerialName("part")
    PART("part"),

    @SerialName("measure")
    MEASURE("measure"),

    @SerialName("note")
    NOTE("note"),

    @SerialName("pitch")
    PITCH("pitch"),

    @SerialName("harmony")
    HARMONY("harmony"),

    @SerialName("lyric")
    LYRIC("lyric"),
}

public interface ValidationIdentifier {
    /** The kind of location this identifier names. `null` only for [NoValidationIdentifier]. */
    public val kind: ValidationLocationKind?
    public val identifier: IdOrIndex
    public val validationIdentifierParent: ValidationIdentifier
}

public object NoValidationIdentifier : ValidationIdentifier {
    public override val kind: ValidationLocationKind? = null
    public override val identifier: IdOrIndex = NoId
    public override val validationIdentifierParent: ValidationIdentifier = this
}

public data object NoId : Id
