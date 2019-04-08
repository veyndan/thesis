package com.veyndan.thesis

import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.random.nextInt

open class Bound(open val value: Double, private val range: ClosedFloatingPointRange<Double>) {

    init {
        require(value in range) { "$value not in $range" }
    }
}

/**
 * Calculates the L<sub>2</sub> (Euclidean) distance between two points.
 *
 * @param p1 the first point
 * @param p2 the second point
 * @return the L<sub>2</sub> distance between the two points
 * @throws IllegalArgumentException if the array lengths differ.
 */
fun euclideanDistance(p1: List<Bound>, p2: List<Bound>): Double {
    require(p1.size == p2.size) { "${p1.size} != ${p2.size}}" }
    return sqrt((p1 zip p2).sumByDouble { (it.first.value - it.second.value).pow(2) })
}

val random = if (DEBUG) Random(0) else Random

fun Random.nextDoubleRange(range: ClosedFloatingPointRange<Double>) = nextDoubleRange(range.start, range.endInclusive)

fun Random.nextDoubleRange(from: Double, until: Double): ClosedFloatingPointRange<Double> {
    val a = nextDouble(from, until)
    val b = nextDouble(from, until)
    return if (a < b) a..b else b..a
}

fun <T> List<T>.sample(n: IntRange) = shuffled().take(random.nextInt(n))
