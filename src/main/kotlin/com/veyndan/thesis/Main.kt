@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis

import com.veyndan.thesis.exchange.Bettor
import com.veyndan.thesis.exchange.Exchange
import com.veyndan.thesis.exchange.Probability
import com.veyndan.thesis.exchange.toOdds
import com.veyndan.thesis.math.mean
import com.veyndan.thesis.math.random
import com.veyndan.thesis.math.sample
import com.veyndan.thesis.race.Competitor
import com.veyndan.thesis.race.Race
import com.veyndan.thesis.race.Track
import com.veyndan.thesis.race.sum
import java.util.concurrent.TimeUnit

fun main() {
    val factorCount = 100

    val competitorPool = List(10, Competitor.generator(factorCount, rangeBounds = 10.0..25.0))
    val trackPool = List(10, Track.generator(factorCount))
    val bettorPool = List(10, Bettor.generator(fundsRange = 5.toPounds()..10.toPounds(), dryRunsRange = 0U..100U))

    val race = Race(trackPool.random(random), competitorPool.sample(2..competitorPool.size))

    val exchange = Exchange(bettorPool.sample(2..bettorPool.size))

    val dryRunProbabilities = exchange.bettors
        .map { bettor -> race.steps().take(bettor.dryRunCount.toInt()).toList() }
        .map { dryRun -> race.competitors.indices.map { i -> dryRun.map { it[i] }.mean() } }
        .map { dryRunMean -> dryRunMean.map { Probability((it / dryRunMean.sum()).value).toOdds() } }

    val dryRunProbabilities2 = exchange.bettors
        .map { bettor -> race.steps().take(bettor.dryRunCount.toInt()).toList() }
        .map { dryRun -> race.competitors.indices.map { i -> dryRun.map { it[i] }.mean() } }
        .map { dryRunMean -> dryRunMean.map { Probability(((race.track.length / it) / dryRunMean.map { race.track.length / it }.sum()).value).toOdds() } }

    dryRunProbabilities.forEach(::println)
    println()

    dryRunProbabilities2.forEach(::println)
    println()

    println("Track(length=${race.track.length.value})")
    println("Competitors(size=${race.competitors.size})")
    println()

    race.positions().forEachIndexed { tick, positions ->
        print("\rtick=$tick ${positions.map { (_, position) -> "${position.value}" }.joinToString(" ")}")

        TimeUnit.MILLISECONDS.sleep(500L)
    }
}
