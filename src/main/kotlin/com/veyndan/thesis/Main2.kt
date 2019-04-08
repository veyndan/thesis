@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis

import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.min

data class Competitor(val variability: ClosedFloatingPointRange<Double>, val preferences: List<Preference>) {

    private fun body(): Double =
        random.nextDouble(variability.start, variability.endInclusive)

    private fun compatibility(factors: List<Track.Factor>): Double =
        abs(1 - euclideanDistance(factors, preferences))

    fun stepSize(factors: List<Track.Factor>): Double =
        body() * compatibility(factors)

    data class Preference(override val value: Double) : Bound(value, 0.0..1.0)

    companion object {

        fun generate(factorCount: Int, rangeBounds: ClosedFloatingPointRange<Double>) = generateSequence {
            Competitor(
                random.nextDoubleRange(rangeBounds),
                List(factorCount) {
                    Preference(
                        random.nextDouble(
                            0.0,
                            1.0
                        )
                    )
                }
            )
        }
    }
}

data class Track(val length: Double, val factors: List<Factor>) {

    data class Factor(override val value: Double) : Bound(value, 0.0..1.0)
}

data class Race(val track: Track, private val competitors: List<Competitor>) {

    fun positions(competitorsDistance: Map<Competitor, Double> = competitors.associateWith { 0.0 }): Sequence<Map<Competitor, Double>> =
        sequenceOf(competitorsDistance) + sequenceOf(competitorsDistance)
            .takeWhile { it.any { (_, distance) -> distance < track.length } }
            .flatMap {
                positions(it.mapValues { (competitor, distance) ->
                    min(distance + competitor.stepSize(track.factors), track.length)
                })
            }
}

/**
 * Allowable stake range in pennies.
 */
val BETTING_LIMIT = 2.pounds..ULong.MAX_VALUE

/**
 * @property funds In pennies.
 */
data class Bettor(val funds: ULong) {

    init {
        require(funds > BETTING_LIMIT.start)
    }

    companion object {

        fun generate(fundsRange: ULongRange) = generateSequence {
            Bettor(fundsRange.random(random))
        }
    }
}

/*
- back list and lay list
- back list ordered by highest value to lowest value
- lay list ordered by lowest value to highest value
- add new back order: IF back order value >= best lay order value THEN
 */

fun main() {
    val factorCount = 100

    val competitors = Competitor.generate(
        factorCount = factorCount,
        rangeBounds = 10.0..25.0
    ).take(10).toList()
    val competitorsSample = competitors.sample(2..Int.MAX_VALUE)

    val race = Race(
        Track(
            500.0,
            List(factorCount) { Track.Factor(random.nextDouble(0.0, 1.0)) }),
        competitorsSample
    )

    val bettors = Bettor.generate(5.pounds..10.pounds).take(10).toList()
    val bettorsSample = bettors.sample(2..Int.MAX_VALUE)

    println("TRACK")
    println(race.track)
    println()

    println("COMPETITORS")
    competitorsSample.forEach(::println)
    println()

    println()

    race.positions().forEachIndexed { tick, positions ->
        print("\rtick=$tick ${positions.map { (_, position) -> "$position" }.joinToString(" ")}")

        TimeUnit.MILLISECONDS.sleep(500L)
    }
}
