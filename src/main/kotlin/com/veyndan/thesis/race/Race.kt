@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis.race

data class Race(val track: Track, val competitors: List<Competitor>) {

//    fun steps(): Sequence<Map<Competitor, Distance>> = generateSequence { competitors.map { it to it.stepSize(track.factors) }.toMap() }

    data class Position(val position: UInt, val distance: Distance)

    fun positions(competitorsDistance: Map<Competitor, Position> = competitors.associateWith { Position(0U, Distance(0.0)) }): Sequence<Map<Competitor, Position>> =
        sequenceOf(competitorsDistance) + sequenceOf(competitorsDistance)
            .takeWhile { it.all { (_, position) -> position.distance < track.length } }
            .flatMap {
                //                val competitorsStep = it.mapValues { (competitor, _) -> competitor.stepSize(track.factors) }
//                val nextDistance = it.mergeReduce(competitorsStep) { distance, step -> Distance(min((distance + step).value, track.length.value)) }
//
//                val multipleWinners = nextDistance.values.countBy().getOrDefault(track.length, 0) > 1
//
//                if (!multipleWinners) {
//                    positions(nextDistance)
//                } else {
//                    val competitorsStep = competitorsStep.mapValues { (_, step) -> step / 2.0 }
//                    val nextDistance = it.mergeReduce(competitorsStep) { distance, step -> Distance(min((distance + step).value, track.length.value)) }
//
//                    val multipleWinners = nextDistance.values.countBy().getOrDefault(track.length, 0) > 1
//
//                    if (!multipleWinners) {
//                        positions(nextDistance)
//                    } else {
//                        val competitorsStep = competitorsStep.mapValues { (_, step) -> step / 2.0 }Z
//                        val nextDistance = it.mergeReduce(competitorsStep) { distance, step -> Distance(min((distance + step).value, track.length.value)) }
//
//                        positions(nextDistance)
//                    }
//
//                    positions(nextDistance)
//                }

                // Generate all the next steps.
                // If when adding all the next steps, no more than one horse is past the finished line, then return these new positions
                // Else, map the steps to half their value, and add them, and check to see if no more than half of the horses are past the finished line.
                // Iteratively do this until a step increment has at most one horse past the finish line
                positions(it.mapValues { (competitor, position) ->
                    Position(0U, min(position.distance + competitor.stepSize(track.factors), track.length))
                })
            }
}
