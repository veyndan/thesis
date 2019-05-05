package com.veyndan.thesis.race

import com.veyndan.thesis.math.Bound
import com.veyndan.thesis.math.euclideanDistance
import com.veyndan.thesis.math.random
import kotlin.math.abs

data class Competitor(val index: Int, val variability: ClosedFloatingPointRange<Double>, val preferences: List<Preference>) {

    private fun body(): Double = random.nextDouble(variability.start, variability.endInclusive)

    private fun compatibility(factors: List<Track.Factor>): Double = abs(1 - euclideanDistance(factors, preferences))

    fun stepSize(factors: List<Track.Factor>): Distance = Distance(body() * compatibility(factors))

    override fun toString() = "Competitor($index)"

    data class Preference(override val value: Double) : Bound(value, 0.0..1.0)

    companion object {

        fun generator(factorCount: Int, rangeBounds: ClosedFloatingPointRange<Double>): (index: Int) -> Competitor = { index ->
            Competitor(
                index,
                rangeBounds,
//                random.nextDoubleRange(rangeBounds),
                List(factorCount) { Preference(random.nextDouble(0.0, 1.0)) }
            ).also { println("${it.index} ${it.variability}")}
        }
    }
}
