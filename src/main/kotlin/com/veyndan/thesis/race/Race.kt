@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis.race

import com.veyndan.thesis.utility.mergeWith

inline class Position(val value: UInt)

data class Race(val track: Track, val competitors: List<Competitor>) {

//    fun steps(): Sequence<Map<Competitor, Distance>> = generateSequence { competitors.map { it to it.stepSize(track.factors) }.toMap() }

    fun positions(competitorsDistance: Map<Competitor, Pair<Position, Distance>> = competitors.associateWith { Position(0U) to Distance(0.0) }): Sequence<Map<Competitor, Pair<Position, Distance>>> =
        sequenceOf(competitorsDistance) + sequenceOf(competitorsDistance)
            .takeWhile { it.all { (_, position) -> position.second < track.length } }
            .flatMap { competitorsDistance ->
                // TODO Correct position
                // TODO If more than one horse has passed the finish line in one timestep, take the horse with the greatest step size.
                val updatedDistance = competitorsDistance.mapValues { (competitor, position) -> min(position.second + competitor.stepSize(track.factors), track.length) }
                val updatedPositions = updatedDistance.entries
                    .sortedByDescending { (_, distance) -> distance }
                    .withIndex()
                    .map { (index, value) -> value.key to Position(index.toUInt()) }
                    .toMap()

//                println(positions.map { (index, value) -> Position(index, value.) })
                positions(updatedPositions.mergeWith(updatedDistance) { position, distance -> position to distance })
            }
}
