package com.veyndan.thesis.race

import com.veyndan.thesis.math.euclideanDistance
import com.veyndan.thesis.math.random
import java.io.Serializable
import kotlin.math.abs
import kotlin.math.log10

class Step(
    private val variabilityStart: Double,
    private val variabilityEndInclusive: Double,
    preferences: List<Competitor.Preference>,
    factors: List<Track.Factor>
) : Serializable {

    constructor(
        variability: ClosedFloatingPointRange<Double>,
        preferences: List<Competitor.Preference>,
        factors: List<Track.Factor>
    ) : this(variability.start, variability.endInclusive, preferences, factors)

    fun variability(): ClosedFloatingPointRange<Double> = variabilityStart..variabilityEndInclusive

    private val compatibility: Double = log10(abs(1 - euclideanDistance(factors, preferences)))

    private fun body(): Double = random.nextDouble(variabilityStart, variabilityEndInclusive)
//    private fun body(): Double = random.nextDouble(5.0, 10.0)

    fun stepSize(): Distance = Distance(body() * compatibility)
}
