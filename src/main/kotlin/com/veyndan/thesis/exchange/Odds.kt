@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis.exchange

import com.veyndan.thesis.math.decimalPlaces
import kotlin.math.roundToLong

/**
 * @property value The odds stored as an integer, where the raw odds are multiplied by 100.
 */
inline class Odds(val value: ULong) : Comparable<Odds> {

    override fun compareTo(other: Odds): Int = value.compareTo(other.value)

    operator fun plus(other: Odds) = Odds(value + other.value)

    operator fun minus(other: Odds) = Odds(value - other.value)

    operator fun rem(other: Odds) = Odds(value % other.value)

    companion object {

        val COMPARATOR_BACK = Comparator<Odds> { o1, o2 -> o1.compareTo(o2) }
        val COMPARATOR_LAY = Comparator<Odds> { o1, o2 -> o2.compareTo(o1) }
    }
}

fun Probability.toOdds() = Odds(((1 / value) * 100).roundToLong().toULong())

fun Int.toOdds(): Odds = toULong().toOdds()

fun Long.toOdds(): Odds = toULong().toOdds()

fun ULong.toOdds(): Odds = Odds(this * 100U)

fun Double.toOdds(): Odds {
    require(decimalPlaces <= 2U)
    return Odds((this * 100).toLong().toULong())
}
