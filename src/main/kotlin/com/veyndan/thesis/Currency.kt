@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis

import com.veyndan.thesis.exchange.Odds
import com.veyndan.thesis.math.decimalPlaces
import java.math.RoundingMode

inline class Pennies(val value: ULong) : Comparable<Pennies> {

    override fun compareTo(other: Pennies): Int = value.compareTo(other.value)

    operator fun plus(other: Pennies) = Pennies(value + other.value)

    operator fun minus(other: Pennies): Pennies = if (value >= other.value) {
        Pennies(value - other.value)
    } else {
        throw InsufficientFundsException(other, this)
    }

    operator fun times(other: Pennies) = Pennies(value * other.value)

    operator fun times(other: Odds) = Pennies((value * other.value).toLong().toBigDecimal().divide(100.toBigDecimal(), RoundingMode.HALF_UP).toLong().toULong())
}

fun Int.toPennies(): Pennies {
    require(this >= 0)
    return toULong().toPennies()
}

fun Long.toPennies(): Pennies {
    require(this >= 0)
    return toULong().toPennies()
}

fun ULong.toPennies(): Pennies = Pennies(this)

fun Int.toPounds(): Pennies {
    require(this >= 0)
    return toULong().toPounds()
}

fun Long.toPounds(): Pennies {
    require(this >= 0)
    return toULong().toPounds()
}

fun ULong.toPounds(): Pennies = (this * 100U).toPennies()

fun Double.toPounds(): Pennies {
    require(this >= 0)
    require(decimalPlaces <= 2U) { "Exchange doesn't support fractional pennies: $this" }
    return (this.toBigDecimal() * 100.toBigDecimal()).toLong().toPennies()
}
