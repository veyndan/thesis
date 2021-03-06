@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis.math

import com.veyndan.thesis.race.Competitor
import com.veyndan.thesis.race.Track
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.nextInt

val NATURAL_NUMBERS = generateSequence(0U) { it + 1U }

val Double.decimalPlaces: UShort
    get() = toString().split('.').last().length.toUShort()

/**
 * Calculates the L<sub>2</sub> (Euclidean) distance between two points.
 *
 * @param p1 the first point
 * @param p2 the second point
 * @return the L<sub>2</sub> distance between the two points
 * @throws IllegalArgumentException if the array lengths differ.
 */
fun euclideanDistance(p1: List<Track.Factor>, p2: List<Competitor.Preference>): Double {
    require(p1.size == p2.size) { "${p1.size} != ${p2.size}}" }
    return sqrt((p1 zip p2).sumByDouble { (it.first.value - it.second.value).pow(2) })
}

fun <T> Iterable<T>.sample(n: IntRange) = if (toMutableList().isEmpty()) emptyList() else shuffled().take(random.nextInt(n))
