@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis.race

import com.veyndan.thesis.math.NATURAL_NUMBERS
import com.veyndan.thesis.math.random
import java.io.Serializable

data class Competitor(
    val index: UInt,
    val variabilityStart: Double,
    val variabilityEndInclusive: Double,
    val preferences: List<Preference>
) : Serializable {

    // Don't make field as can't serialize Range.
    fun variability(): ClosedFloatingPointRange<Double> = variabilityStart..variabilityEndInclusive

    override fun toString() = "Competitor($index)"

    data class Preference(val value: Double) : Serializable

    companion object {

        fun generate(factorCount: Int, variability: () -> ClosedFloatingPointRange<Double>) = NATURAL_NUMBERS.map(generator(factorCount, variability))

        private fun generator(factorCount: Int, variability: () -> ClosedFloatingPointRange<Double>): (index: UInt) -> Competitor = { index ->
            val variability = variability()
            Competitor(
                index,
                variability.start,
                variability.endInclusive,
                List(factorCount) { Preference(random.nextDouble(0.0, 1.0)) }
            )
        }
    }
}
