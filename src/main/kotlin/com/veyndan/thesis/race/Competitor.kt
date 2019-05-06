package com.veyndan.thesis.race

import com.veyndan.thesis.math.Bound
import com.veyndan.thesis.math.random

data class Competitor(val index: Int, val variability: ClosedFloatingPointRange<Double>, val preferences: List<Preference>) {

    override fun toString() = "Competitor($index)"

    data class Preference(override val value: Double) : Bound(value, 0.0..1.0)

    companion object {

        fun generator(factorCount: Int, variability: () -> ClosedFloatingPointRange<Double>): (index: Int) -> Competitor = { index ->
            Competitor(
                index,
                variability(),
                List(factorCount) { Preference(random.nextDouble(0.0, 1.0)) }
            )
        }
    }
}
