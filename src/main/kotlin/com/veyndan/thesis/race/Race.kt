package com.veyndan.thesis.race

import kotlin.math.min

data class Race(val track: Track, private val competitors: List<Competitor>) {

    fun positions(competitorsDistance: Map<Competitor, Distance> = competitors.associateWith { Distance(0.0) }): Sequence<Map<Competitor, Distance>> =
        sequenceOf(competitorsDistance) + sequenceOf(competitorsDistance)
            .takeWhile { it.any { (_, distance) -> distance.value < track.length.value } }
            .flatMap {
                positions(it.mapValues { (competitor, distance) ->
                    Distance(min(distance.value + competitor.stepSize(track.factors), track.length.value))
                })
            }
}
