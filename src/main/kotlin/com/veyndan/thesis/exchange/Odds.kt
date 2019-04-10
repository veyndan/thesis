@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis.exchange

import com.veyndan.thesis.requireMaxDecimalPlacesAllowed

/**
 * @property value The odds stored as an integer, where the raw odds are multiplied by 100.
 */
inline class Odds(val value: ULong) {

    companion object {

        val COMPARATOR_BACK = Comparator<Odds> { o1, o2 -> o1.value.compareTo(o2.value) }
        val COMPARATOR_LAY = Comparator<Odds> { o1, o2 -> o2.value.compareTo(o1.value) }
    }
}

fun Int.toOdds(): Odds = toULong().toOdds()

fun Long.toOdds(): Odds = toULong().toOdds()

fun ULong.toOdds(): Odds = Odds(this * 100U)

fun Double.toOdds(): Odds {
    requireMaxDecimalPlacesAllowed(2U)
    return Odds((this * 100).toLong().toULong())
}
