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

package com.sparetimedevs.ami.music.data.kotlin.fraction

import com.sparetimedevs.ami.core.validation.ValidationException
import com.sparetimedevs.ami.music.data.kotlin.timesignature.BeatType
import com.sparetimedevs.ami.music.data.kotlin.timesignature.Beats
import com.sparetimedevs.ami.music.data.kotlin.timesignature.TimeSignature
import com.sparetimedevs.ami.music.input.validation.validate
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import com.sparetimedevs.ami.music.serialization.Fraction as SerializableFraction

class FractionTest {
    private fun properties(
        numerator: Int,
        denominator: Int,
    ) = Fraction.validate(numerator, denominator).shouldBeLeft().map { it.validationErrorForProperty.value }

    @Test
    fun `a reduced fraction is valid`() {
        Fraction.validate(3, 4).shouldBeRight().toString() shouldBe "3/4"
        Fraction.validate(0, 1).shouldBeRight() shouldBe Fraction.ZERO
        Fraction.validate(5, 4).shouldBeRight().toString() shouldBe "5/4"
    }

    @Test
    fun `a denominator of zero or less is an error on Denominator`() {
        properties(1, 0) shouldBe listOf("Denominator")
        properties(1, -2) shouldBe listOf("Denominator")
    }

    @Test
    fun `a negative numerator is an error on Numerator`() {
        properties(-1, 2) shouldBe listOf("Numerator")
    }

    @Test
    fun `numerator and denominator errors accumulate`() {
        properties(-1, 0) shouldBe listOf("Denominator", "Numerator")
    }

    @Test
    fun `an unreduced fraction is an error on Fraction`() {
        properties(2, 4) shouldBe listOf("Fraction")
        properties(0, 2) shouldBe listOf("Fraction")
    }

    @Test
    fun `the generated fraction validates through the extension`() {
        SerializableFraction(1, 2).validate().shouldBeRight() shouldBe Fraction.of(1, 2)
        SerializableFraction(2, 4)
            .validate()
            .shouldBeLeft()
            .single()
            .validationErrorForProperty.value shouldBe
            "Fraction"
    }

    @Test
    fun `of reduces`() {
        Fraction.of(2, 4) shouldBe Fraction.unsafeCreate(1, 2)
        Fraction.of(0, 8) shouldBe Fraction.ZERO
        Fraction.of(6, 6) shouldBe Fraction.ONE
        shouldThrow<ValidationException> { Fraction.of(1, 0) }
        shouldThrow<ValidationException> { Fraction.of(-1, 2) }
    }

    @Test
    fun `unsafeCreate throws on invalid input`() {
        shouldThrow<ValidationException> { Fraction.unsafeCreate(2, 4) }
    }

    @Test
    fun `fractions compare by value`() {
        (Fraction.of(1, 3) < Fraction.of(1, 2)) shouldBe true
        (Fraction.of(3, 4) > Fraction.of(2, 3)) shouldBe true
        Fraction.of(1, 4096).compareTo(Fraction.of(1, 4095)) shouldBe -1
        Fraction.of(5, 4) compareTo Fraction.of(5, 4) shouldBe 0
    }

    @Test
    fun `addition reduces and goes past a whole note`() {
        Fraction.of(1, 4) + Fraction.of(1, 4) shouldBe Fraction.of(1, 2)
        Fraction.of(3, 4) + Fraction.of(3, 4) shouldBe Fraction.unsafeCreate(3, 2)
        Fraction.of(1, 4096) + Fraction.of(1, 4096) shouldBe Fraction.unsafeCreate(1, 2048)
        Fraction.of(1, 4096) + Fraction.of(1, 3) shouldBe Fraction.unsafeCreate(4099, 12288)
    }

    @Test
    fun `subtraction reduces and fails below zero`() {
        Fraction.of(3, 4) - Fraction.of(1, 4) shouldBe Fraction.of(1, 2)
        Fraction.of(1, 2) - Fraction.of(1, 2) shouldBe Fraction.ZERO
        Fraction.of(1, 2) - Fraction.of(1, 4096) shouldBe Fraction.unsafeCreate(2047, 4096)
        shouldThrow<ValidationException> { Fraction.of(1, 4096) - Fraction.of(1, 2048) }
    }

    @Test
    fun `measure length of a time signature`() {
        fun length(
            beats: Byte,
            beatType: Byte,
        ) = TimeSignature(Beats.unsafeCreate(beats), BeatType.unsafeCreate(beatType)).measureLength

        length(4, 4) shouldBe Fraction.ONE
        length(3, 4) shouldBe Fraction.unsafeCreate(3, 4)
        length(6, 8) shouldBe Fraction.unsafeCreate(3, 4)
        length(2, 2) shouldBe Fraction.ONE
    }
}
