package com.veyndan.thesis.race

import com.veyndan.thesis.math.euclideanDistance
import com.veyndan.thesis.math.random
import kotlin.math.abs
import kotlin.math.ln

class Step(
    private val variability: ClosedFloatingPointRange<Double>,
    preferences: List<Competitor.Preference>,
    factors: List<Track.Factor>
) {

    private val compatibility: Double = ln(abs(1 - euclideanDistance(factors, preferences)))

    private fun body(): Double = random.nextDouble(variability.start, variability.endInclusive)

    fun stepSize(): Distance = Distance(body() * compatibility)
}
