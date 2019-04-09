package com.veyndan.thesis.math

import com.veyndan.thesis.DEBUG
import kotlin.random.Random

val random = if (DEBUG) Random(0) else Random

fun Random.nextDoubleRange(range: ClosedFloatingPointRange<Double>) = nextDoubleRange(range.start, range.endInclusive)

fun Random.nextDoubleRange(from: Double, until: Double): ClosedFloatingPointRange<Double> {
    val a = nextDouble(from, until)
    val b = nextDouble(from, until)
    return if (a < b) a..b else b..a
}
