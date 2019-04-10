@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis

inline class Pennies(val value: ULong)

fun Int.toPennies(): Pennies = toULong().toPennies()

fun Long.toPennies(): Pennies = toULong().toPennies()

fun ULong.toPennies(): Pennies = Pennies(this)

fun Int.toPounds(): Pennies = toULong().toPounds()

fun Long.toPounds(): Pennies = toULong().toPounds()

fun ULong.toPounds(): Pennies = (this * 100U).toPennies()

fun Double.toPounds(): Pennies {
    requireMaxDecimalPlacesAllowed(2U) { "Exchange doesn't support fractional pennies: $this" }
    return (this * 100).toLong().toPennies()
}
