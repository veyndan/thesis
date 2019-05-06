@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis.race

import com.veyndan.thesis.math.sample
import com.veyndan.thesis.utility.mergeWith
import com.veyndan.thesis.utility.toMap
import kotlin.math.E
import kotlin.math.pow

inline class Position(val value: UInt) : Comparable<Position> {

    operator fun plus(other: Position) = Position(value + other.value)

    override fun compareTo(other: Position) = value.compareTo(other.value)

    override fun toString() = "Position($value)"
}

data class Race(val track: Track, val competitors: List<Competitor>) {

    private val steps = competitors
        .map { competitor ->
            competitor to Step(competitor.variability, competitor.preferences, track.factors)
        }
        .toMap()

    fun positions(competitorsDistance: Map<Competitor, Pair<Position, Distance>> = competitors.associateWith { Position(0U) to Distance(0.0) }): Sequence<Map<Competitor, Pair<Position, Distance>>> =
        sequenceOf(competitorsDistance) + sequenceOf(competitorsDistance)
            .takeWhile { it.all { (_, pair) -> pair.second < track.length } }
            .flatMap { competitorsDistance ->
                val deltas = competitorsDistance.mapValues { entry ->
                    val aheadCompetitors = competitorsDistance
                        .filter { it != entry }
                        .filterValues { pair -> pair.first <= entry.value.first }

                    val hinderingCompetitors = aheadCompetitors.entries.sample(aheadCompetitors.entries.indices).toMap()

                    val hinderingCompetitorsDistanceAhead = hinderingCompetitors.values.map { (it.second - entry.value.second).value }

                    val g = 1.0 - (hinderingCompetitorsDistanceAhead.sumByDouble { distance -> E.pow(-distance) } / competitors.size)

                    steps.getValue(entry.key).stepSize() * g
                }

                val distances = competitorsDistance.mergeWith(deltas) { pair, delta -> min(pair.second + delta, track.length) }

                val groupedDistances = distances.entries
                    .sortedByDescending { (_, distance) -> distance }
                    .groupBy { it.value }
                    .mapValues { it.value.toMap() }
                    .entries
                    .withIndex()
                    .flatMap { (index, value) -> value.value.mapValues { Position(index.toUInt()) to it.value }.entries }
                    .toMap()

                val winners = groupedDistances.filterValues { (position, distance) -> position == Position(0U) && distance == track.length }
                val losers = groupedDistances.filterValues { (position, distance) -> position != Position(0U) || distance != track.length }

                if (winners.size <= 1) {
                    positions(groupedDistances)
                } else {
                    // If more than one winner, reassign the positions of the winners such that only the horse with the highest delta is given first place.

                    val updatedWinnerPositions = winners
                        .mapValues { (competitor, _) -> deltas.getValue(competitor) }
                        .entries
                        .sortedByDescending { it.value }
                        .mapIndexed { index, entry -> entry.key to (Position(index.toUInt()) to distances.getValue(entry.key)) }
                        .toMap()

                    val updatedLoserPositions = losers.mapValues { (_, pair) -> (pair.first + Position(winners.size.toUInt() - 1U)) to pair.second }

                    positions(updatedWinnerPositions + updatedLoserPositions)
                }
            }
}
