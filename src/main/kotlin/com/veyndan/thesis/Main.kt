@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis

import com.veyndan.thesis.exchange.Bettor
import com.veyndan.thesis.exchange.Exchange
import com.veyndan.thesis.exchange.Probability
import com.veyndan.thesis.math.random
import com.veyndan.thesis.math.sample
import com.veyndan.thesis.race.*
import com.veyndan.thesis.utility.take
import java.util.concurrent.TimeUnit

fun main() {
    val factorCount = 100

    val competitorPool = List(10, Competitor.generator(factorCount, rangeBounds = 10.0..25.0))
    val trackPool = List(10, Track.generator(factorCount))
    val bettorPool = List(10, Bettor.generator(fundsRange = 5.toPounds()..10.toPounds(), dryRunsRange = 0U..100U))

    val race = Race(trackPool.random(random), competitorPool.sample(2..competitorPool.size))

    val exchange = Exchange(bettorPool.sample(2..bettorPool.size))

    println("Track(length=${race.track.length.value})")
    println("Competitors(size=${race.competitors.size})")
    println()

    race.positions().forEachIndexed { tick, positions ->
        println("tick=$tick ${positions.map { (_, position) -> "${position.value}" }.joinToString(" ")}")

        val dryRunProbabilities = exchange.bettors.asSequence()
            .map { bettor -> race.steps().take(bettor.dryRunCount) }
            .map { dryRuns -> dryRuns.averageBy() }
            .map { dryRunMeans ->
                dryRunMeans.map { (competitor, distance) ->
                    val numerator = (race.track.length - positions.getValue(competitor)) / distance
                    val denominator =
                        dryRunMeans.map { (race.track.length - positions.getValue(it.key)) / it.value }.sum()
                    val probability = Probability((numerator / denominator).value)
                    competitor to probability
                }.toMap()
            }

        println(dryRunProbabilities.first().map { it.value.value })

        TimeUnit.MILLISECONDS.sleep(500L)

        println()
    }
}
