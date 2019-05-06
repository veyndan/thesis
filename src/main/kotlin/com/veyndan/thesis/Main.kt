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
    // MODIFICATION OF racePool MUST
    val racePool = RacePool(
        competitorsSize = 10000,
        tracksSize = 10,
        factorCount = 100,
        competitorChunking = { random.nextInt(6..10) }
    ).read()

    val bettorPool = List(10, Bettor.generator(fundsRange = 5.toPounds()..10.toPounds(), dryRunsRange = 0U..20U))

    val race = racePool[0]

    val market = Market(bettorPool.take(2))
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
