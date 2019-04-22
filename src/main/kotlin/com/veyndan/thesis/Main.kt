@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis

import com.veyndan.thesis.exchange.Bettor
import com.veyndan.thesis.exchange.Exchange
import com.veyndan.thesis.math.random
import com.veyndan.thesis.race.Competitor
import com.veyndan.thesis.race.Race
import com.veyndan.thesis.race.Track
import com.veyndan.thesis.race.descriptiveStatisticsBy
import com.veyndan.thesis.utility.take
import org.apache.commons.math3.distribution.NormalDistribution

fun main() {
    val factorCount = 100

    val competitorPool = List(10, Competitor.generator(factorCount, rangeBounds = 10.0..25.0))
    val trackPool = List(10, Track.generator(factorCount))
    val bettorPool = List(10, Bettor.generator(fundsRange = 5.toPounds()..10.toPounds(), dryRunsRange = 0U..100U))

//    val race = Race(trackPool.random(random), competitorPool.sample(2..competitorPool.size))
    val race = Race(trackPool.random(random), competitorPool.take(10))

//    val exchange = Exchange(bettorPool.sample(2..bettorPool.size))
    val exchange = Exchange(bettorPool.take(1))

    val dryRunDescriptiveStatistics = exchange.bettors.asSequence()
        .map { bettor -> race.steps().take(bettor.dryRunCount) }
        .map { dryRuns -> dryRuns.descriptiveStatisticsBy() }
        .toList()

    dryRunDescriptiveStatistics.first()
        .map { NormalDistribution(it.value.mean, it.value.standardDeviation) }
        .forEach { og ->
            // This is like a cumulative distribution. Can I use these comparing other competitors cumulative distributions to see who's more likely to win?
            (1..Int.MAX_VALUE).asSequence()
                .map { i -> NormalDistribution(og.mean * i, og.standardDeviation * i) }
                .map { normalDistribution -> 1 - normalDistribution.cumulativeProbability(race.track.length.value) }
                .onEach { println(it) }
                .takeWhile { probability -> probability < 0.99999 }
                .forEach {}

            println("\nBOOTY\n")
        }

//    println("Track(length=${race.track.length.value})")
//    println("Competitors(size=${race.competitors.size})")
//    println()
//
//    val dryRunAverages = exchange.bettors.asSequence()
//        .map { bettor -> race.steps().take(bettor.dryRunCount) }
//        .map { dryRuns -> dryRuns.averageBy() }
//        .toList()
//
//    println("DryRunAverages(${dryRunAverages.flatMap { it.values }.map { it.value }})")
//
//    race.positions().forEachIndexed { tick, positions ->
//        val dryRunProbabilities = dryRunAverages.map { dryRunAverages ->
//            val totalRemainingSteps = dryRunAverages
//                .map { (race.track.length - positions.getValue(it.key)) / it.value.value }
//                .sum()
//
//            println("TOTAL REMAINING STEPS ${totalRemainingSteps.value}")
//
//            dryRunAverages.mapValues { (competitor, distance) ->
//                val remainingSteps = (race.track.length - positions.getValue(competitor)) / distance.value
//                Probability(remainingSteps / totalRemainingSteps)
//            }
//        }
//
//        val decimalFormat = DecimalFormat("#0.000")
//
//        val tickString = "Time ${tick.toString().padStart(4)}"
//        val distanceMapString = positions.mapValues { "distance=${decimalFormat.format(it.value.value).padStart(8)}" }
//        val oddsMapString = dryRunProbabilities
//            .map { it.mapValues { decimalFormat.format(it.value.value) } }
//            .toMultimap()
//
//        val lines = (distanceMapString.zip(oddsMapString) { a, b -> a to "odds=$b" }).values
//
//        println("$tickString\n${lines.joinToString("\n")}\n")
//
////        TimeUnit.MILLISECONDS.sleep(500L)
//    }
}
