@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis

import com.veyndan.thesis.exchange.Bettor
import com.veyndan.thesis.exchange.Market
import com.veyndan.thesis.math.random
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.nield.kotlinstatistics.countBy
import kotlin.random.nextInt

fun main() {
    val racePool = RacePool(
        competitorsSize = 5000,
        tracksSize = 1,
        factorCount = 100,
        simulations = 100,
        competitorVariability = { random.nextDouble(9.99, 9.995)..random.nextDouble(9.995, 10.0) }, // DELETE CACHE WHEN MODIFYING
        competitorChunking = { random.nextInt(6..10) } // DELETE CACHE WHEN MODIFYING
    ).read()

//    val bettorPool = List(10, Bettor.generator(fundsRange = 5.toPounds()..10.toPounds(), dryRunsRange = 20U..50U))
    val bettorPool = List(5) { Bettor(Bettor.Id(it.toULong()), 10.toPounds(), it.toUInt() * 5U) }

    val race = racePool.first()

    val market = Market(bettorPool)
//    val market = Market(bettorPool.sample(2..bettorPool.size))

    println("Track(length=${race.track.length.value})")
    println("Competitors(size=${race.competitors.size})")
    println("Bettors(size=${market.bettors.size})")
    println()

    race.competitors.forEach { println("$it ${it.variability()}") }
    println()

    race.positions().forEachIndexed { tick, positions ->
        println("tick=$tick")
        positions.forEach(::println)

        val bettorsOdds = market.bettors
            .associate { bettor ->
                val odds = if (bettor.dryRunCount == 0U) {
                    race.competitors
                        .map { it to race.competitors.size.toDouble() }
                        .toMap()
                } else {
                    Flowable.range(0, bettor.dryRunCount.toInt()).parallel()
                        .runOn(Schedulers.computation())
                        .map { race.positions(positions).last().mapValues { it.value.first }.minBy { (_, position) -> position }!!.key }
                        .sequential()
                        .blockingIterable()
                        .countBy()
                        .mapValues { bettor.dryRunCount.toDouble() / it.value.toDouble() }
                }

                bettor to odds
            }

        bettorsOdds.forEach(::println)

        println()
    }
}
