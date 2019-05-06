package com.veyndan.thesis.race

import com.veyndan.thesis.math.random
import java.io.Serializable

data class Competitor(
    val index: Int,
    val variabilityStart: Double,
    val variabilityEndInclusive: Double,
    val preferences: List<Preference>
) : Serializable {

    // Don't make field as can't serialize Range.
    fun variability(): ClosedFloatingPointRange<Double> = variabilityStart..variabilityEndInclusive

    override fun toString() = "Competitor($index)"

    data class Preference(val value: Double) : Serializable

    companion object {

        fun generator(factorCount: Int, variability: () -> ClosedFloatingPointRange<Double>): (index: Int) -> Competitor = { index ->
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
