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

import com.sparetimedevs.ami.core.Index
import com.sparetimedevs.ami.core.validation.ValidationIdentifier
import com.sparetimedevs.ami.music.data.kotlin.part.PartId
import com.sparetimedevs.ami.music.data.kotlin.score.ScoreId

public data class ValidationIdentifierForScore(
    public override val identifier: ScoreId,
    public override val validationIdentifierParent: ValidationIdentifier
) : ValidationIdentifier

public data class ValidationIdentifierForPart(
    public override val identifier: PartId,
    public override val validationIdentifierParent: ValidationIdentifier
) : ValidationIdentifier

public data class ValidationIdentifierForMeasure(
    public val measureIndex: Int,
    public override val validationIdentifierParent: ValidationIdentifier
) : ValidationIdentifier {
    public override val identifier: Index = Index(measureIndex)
}

public data class ValidationIdentifierForNote(
    public val noteIndex: Int,
    public override val validationIdentifierParent: ValidationIdentifier
) : ValidationIdentifier {
    public override val identifier: Index = Index(noteIndex)
}

public data class ValidationIdentifierForPitchOfChord(
    public val pitchIndex: Int,
    public override val validationIdentifierParent: ValidationIdentifier
) : ValidationIdentifier {
    public override val identifier: Index = Index(pitchIndex)
}
