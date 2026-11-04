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

import arrow.core.Either
import arrow.core.EitherNel
import arrow.core.NonEmptyList
import arrow.core.right
import com.sparetimedevs.ami.core.validation.NoValidationIdentifier
import com.sparetimedevs.ami.core.validation.ValidationError
import com.sparetimedevs.ami.core.validation.ValidationErrorForProperty
import com.sparetimedevs.ami.core.validation.ValidationIdentifier
import com.sparetimedevs.ami.core.validation.getOrThrowFirstValidationError
import com.sparetimedevs.ami.core.validation.validationErrorForProperty
import kotlin.math.abs

/**
 * A non-negative rational number in lowest terms, e.g. a position in a measure as a fraction of a whole note.
 * Zero is `0/1`. Create one with [validate], [unsafeCreate] or, reducing the input, [of].
 */
public class Fraction private constructor(
    public val numerator: Int,
    public val denominator: Int,
) : Comparable<Fraction> {
    override fun compareTo(other: Fraction): Int =
        (numerator.toLong() * other.denominator).compareTo(other.numerator.toLong() * denominator)

    public operator fun plus(other: Fraction): Fraction =
        unsafeCreateReduced(
            numerator.toLong() * other.denominator + other.numerator.toLong() * denominator,
            denominator.toLong() * other.denominator,
        )

    /** Fails like [unsafeCreate] when the result would be below zero. */
    public operator fun minus(other: Fraction): Fraction =
        unsafeCreateReduced(
            numerator.toLong() * other.denominator - other.numerator.toLong() * denominator,
            denominator.toLong() * other.denominator,
        )

    override fun equals(other: Any?): Boolean =
        other is Fraction && numerator == other.numerator && denominator == other.denominator

    override fun hashCode(): Int = 31 * numerator + denominator

    override fun toString(): String = "$numerator/$denominator"

    public companion object {
        public val ZERO: Fraction = Fraction(0, 1)
        public val ONE: Fraction = Fraction(1, 1)

        public const val NUMERATOR_PROPERTY: String = "Numerator"
        public const val DENOMINATOR_PROPERTY: String = "Denominator"

        /** Accepts only a denominator above 0, a numerator of at least 0 and lowest terms. */
        public fun validate(
            numerator: Int,
            denominator: Int,
            validationIdentifier: ValidationIdentifier = NoValidationIdentifier,
        ): EitherNel<ValidationError, Fraction> {
            val errors = mutableListOf<ValidationError>()
            if (denominator <= 0) {
                errors +=
                    ValidationError(
                        "Denominator must be bigger than zero, the input was $denominator",
                        ValidationErrorForProperty(DENOMINATOR_PROPERTY),
                        validationIdentifier,
                    )
            }
            if (numerator < 0) {
                errors +=
                    ValidationError(
                        "Numerator can't be negative, the input was $numerator",
                        ValidationErrorForProperty(NUMERATOR_PROPERTY),
                        validationIdentifier,
                    )
            }
            if (errors.isEmpty() && gcd(numerator.toLong(), denominator.toLong()) != 1L) {
                errors +=
                    ValidationError(
                        "Fraction $numerator/$denominator is not in lowest terms",
                        validationErrorForProperty<Fraction>(),
                        validationIdentifier,
                    )
            }
            return if (errors.isEmpty()) {
                Fraction(numerator, denominator).right()
            } else {
                Either.Left(NonEmptyList(errors.first(), errors.drop(1)))
            }
        }

        public fun unsafeCreate(
            numerator: Int,
            denominator: Int,
        ): Fraction = validate(numerator, denominator, NoValidationIdentifier).getOrThrowFirstValidationError()

        /** Reduces the input: `of(2, 4)` is `1/2`. Fails like [unsafeCreate] on a negative numerator or a denominator of 0 or less. */
        public fun of(
            numerator: Int,
            denominator: Int,
        ): Fraction = unsafeCreateReduced(numerator.toLong(), denominator.toLong())

        private fun unsafeCreateReduced(
            numerator: Long,
            denominator: Long,
        ): Fraction {
            val divisor = gcd(abs(numerator), abs(denominator)).coerceAtLeast(1L)
            val reducedNumerator = numerator / divisor
            val reducedDenominator = denominator / divisor
            if (reducedNumerator !in Int.MIN_VALUE..Int.MAX_VALUE ||
                reducedDenominator !in Int.MIN_VALUE..Int.MAX_VALUE
            ) {
                throw ArithmeticException("Fraction $reducedNumerator/$reducedDenominator doesn't fit in an Int")
            }
            return unsafeCreate(reducedNumerator.toInt(), reducedDenominator.toInt())
        }

        private tailrec fun gcd(
            a: Long,
            b: Long,
        ): Long = if (b == 0L) a else gcd(b, a % b)
    }
}
