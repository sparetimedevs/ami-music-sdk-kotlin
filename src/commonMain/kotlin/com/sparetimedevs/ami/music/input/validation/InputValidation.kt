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
import com.sparetimedevs.ami.core.DomainError
import com.sparetimedevs.ami.core.asEitherWithAccumulatedValidationErrors
import com.sparetimedevs.ami.core.validation.NoValidationIdentifier
import com.sparetimedevs.ami.music.data.kotlin.score.Score

public fun com.sparetimedevs.ami.music.input.Score.validateInput(): Either<DomainError, Score> =
    this
        .validate(validationIdentifier = NoValidationIdentifier)
        .asEitherWithAccumulatedValidationErrors()
