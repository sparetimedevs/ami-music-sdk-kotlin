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

import com.sparetimedevs.ami.core.Index
import com.sparetimedevs.ami.core.validation.ValidationIdentifier
import com.sparetimedevs.ami.core.validation.ValidationLocationKind
import com.sparetimedevs.ami.music.data.kotlin.part.PartId
import com.sparetimedevs.ami.music.data.kotlin.score.ScoreId

public data class ValidationIdentifierForScore(
    public override val identifier: ScoreId,
    public override val validationIdentifierParent: ValidationIdentifier,
) : ValidationIdentifier {
    public override val kind: ValidationLocationKind = ValidationLocationKind.SCORE
}

public data class ValidationIdentifierForPart(
    public override val identifier: PartId,
    public override val validationIdentifierParent: ValidationIdentifier,
) : ValidationIdentifier {
    public override val kind: ValidationLocationKind = ValidationLocationKind.PART
}

public data class ValidationIdentifierForMeasure(
    public val measureIndex: Int,
    public override val validationIdentifierParent: ValidationIdentifier,
) : ValidationIdentifier {
    public override val kind: ValidationLocationKind = ValidationLocationKind.MEASURE
    public override val identifier: Index = Index(measureIndex)
}

public data class ValidationIdentifierForNote(
    public val noteIndex: Int,
    public override val validationIdentifierParent: ValidationIdentifier,
) : ValidationIdentifier {
    public override val kind: ValidationLocationKind = ValidationLocationKind.NOTE
    public override val identifier: Index = Index(noteIndex)
}

public data class ValidationIdentifierForPitchOfChord(
    public val pitchIndex: Int,
    public override val validationIdentifierParent: ValidationIdentifier,
) : ValidationIdentifier {
    public override val kind: ValidationLocationKind = ValidationLocationKind.PITCH
    public override val identifier: Index = Index(pitchIndex)
}

public data class ValidationIdentifierForHarmony(
    public val harmonyIndex: Int,
    public override val validationIdentifierParent: ValidationIdentifier,
) : ValidationIdentifier {
    public override val kind: ValidationLocationKind = ValidationLocationKind.HARMONY
    public override val identifier: Index = Index(harmonyIndex)
}

public data class ValidationIdentifierForLyric(
    public val lyricIndex: Int,
    public override val validationIdentifierParent: ValidationIdentifier,
) : ValidationIdentifier {
    public override val kind: ValidationLocationKind = ValidationLocationKind.LYRIC
    public override val identifier: Index = Index(lyricIndex)
}

public data class ValidationIdentifierForTextDirection(
    public val textDirectionIndex: Int,
    public override val validationIdentifierParent: ValidationIdentifier,
) : ValidationIdentifier {
    public override val kind: ValidationLocationKind = ValidationLocationKind.TEXT_DIRECTION
    public override val identifier: Index = Index(textDirectionIndex)
}

public data class ValidationIdentifierForLineBreak(
    public val lineBreakIndex: Int,
    public override val validationIdentifierParent: ValidationIdentifier,
) : ValidationIdentifier {
    public override val kind: ValidationLocationKind = ValidationLocationKind.LINE_BREAK
    public override val identifier: Index = Index(lineBreakIndex)
}

public data class ValidationIdentifierForSectionStart(
    public val sectionStartIndex: Int,
    public override val validationIdentifierParent: ValidationIdentifier,
) : ValidationIdentifier {
    public override val kind: ValidationLocationKind = ValidationLocationKind.SECTION_START
    public override val identifier: Index = Index(sectionStartIndex)
}
