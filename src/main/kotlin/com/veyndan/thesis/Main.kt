@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis

import com.veyndan.thesis.exchange.Bettor
import com.veyndan.thesis.exchange.Market
import com.veyndan.thesis.math.random
import com.veyndan.thesis.race.Competitor
import com.veyndan.thesis.race.Race
import com.veyndan.thesis.race.Track

fun main() {
    val factorCount = 100

    val competitorPool = List(10, Competitor.generator(factorCount, rangeBounds = 7.0..8.0))
    val trackPool = List(10, Track.generator(factorCount))
    val bettorPool = List(10, Bettor.generator(fundsRange = 5.toPounds()..10.toPounds(), dryRunsRange = 0U..100U))

//    val race = Race(trackPool.random(random), competitorPool.sample(2..competitorPool.size))
    val race = Race(trackPool.random(random), competitorPool.take(10))

//    val exchange = Exchange(bettorPool.sample(2..bettorPool.size))
    val exchange = Market(bettorPool.take(1))

    println("Track Length: ${race.track.length}")

//    val dryRunDescriptiveStatistics = exchange.bettors.asSequence()
//        .map { bettor -> race.steps().take(bettor.dryRunCount) }
//        .map { dryRuns -> dryRuns.descriptiveStatisticsBy() }
//        .toList()

//    val a = dryRunDescriptiveStatistics.first()
//        .map { NormalDistribution(it.value.mean, it.value.standardDeviation) }
//        .map { og ->
//            (1..Int.MAX_VALUE).asSequence()
//                .map { i -> NormalDistribution(og.mean * i, og.standardDeviation * i) }
//                .map { normalDistribution -> 1 - normalDistribution.cumulativeProbability(race.track.length.value) }
//                .takeWhile { probability -> probability < 0.9995 }
//                .onEach(::println)
//                .toList()
//        }

//    a.forEach {  }

//    val max = a.maxBy { it.size }!!
//
//    val b = a.map { it + List(max.size - it.size) { 1.0 } }
//
//    val c = (0..4).map { index ->
//        zip(*b.toTypedArray())
//            .filter { it.any { it in 0.001..0.999 } }
//            .map { it[index] / (it.sum()) }
//    }

//    println(c.map { it.average() }.onEach { println(it) }.sum())

    race.positions().forEachIndexed { tick, positions ->
        println("$tick $positions")

//        val a = (0 until 100)
//            .toFlowable()
//            .subscribeOn(Schedulers.computation())
//            .observeOn(Schedulers.computation())
//            .map { race.positions().last().maxBy { (_, position) -> position }!!.key }
//            .toList()
//            .subscribe { dryRunWinners -> println(dryRunWinners.countBy()) }
//
//        while (!a.isDisposed) {
//        }

//        println(race.positions(positions).last().values.map { it.value }.countBy().filterValues { it > 1 })

//        println(
//            (0 until 10)
//                .map { race.positions().last().maxBy { (_, position) -> position }!!.key }
//                .countBy()
//        )
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
