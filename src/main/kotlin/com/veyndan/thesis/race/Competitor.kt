package com.veyndan.thesis.race

import com.veyndan.thesis.Bound
import com.veyndan.thesis.euclideanDistance
import com.veyndan.thesis.nextDoubleRange
import com.veyndan.thesis.random
import kotlin.math.abs

data class Competitor(val variability: ClosedFloatingPointRange<Double>, val preferences: List<Preference>) {

    private fun body(): Double = random.nextDouble(variability.start, variability.endInclusive)

    private fun compatibility(factors: List<Track.Factor>): Double = abs(1 - euclideanDistance(factors, preferences))

    fun stepSize(factors: List<Track.Factor>): Double = body() * compatibility(factors)

    data class Preference(override val value: Double) : Bound(value, 0.0..1.0)

    companion object {

        fun generate(factorCount: Int, rangeBounds: ClosedFloatingPointRange<Double>) = generateSequence {
            Competitor(
                random.nextDoubleRange(rangeBounds),
                List(factorCount) { Preference(random.nextDouble(0.0, 1.0)) }
            )
        }
    }
}
