@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis

import com.veyndan.thesis.exchange.Bettor
import com.veyndan.thesis.exchange.Market
import com.veyndan.thesis.math.nextDoubleRange
import com.veyndan.thesis.math.random
import com.veyndan.thesis.math.sample
import com.veyndan.thesis.race.Competitor
import com.veyndan.thesis.race.Race
import com.veyndan.thesis.race.Track
import com.veyndan.thesis.utility.toMap
import org.nield.kotlinstatistics.countBy
import kotlin.math.E
import kotlin.math.pow

fun main() {
    val factorCount = 100

    val competitorPool = List(1000, Competitor.generator(factorCount, variability = { random.nextDoubleRange(5.0..10.0) }))
    val trackPool = List(10, Track.generator(factorCount))
    val bettorPool = List(10, Bettor.generator(fundsRange = 5.toPounds()..10.toPounds(), dryRunsRange = 0U..100U))

    val raceForSimilarity = Race(trackPool[0], competitorPool)

    // Can write in the report how this is like a sports league with several rounds but simplified to having
    // every horse race play once. Will have to comment on reason for doing it this way as dry run count is
    // effectively one. Can say that doing too many leagues will mean that dry run counts using bettors will
    // be redundant.
    val competitorPoolBySimilarity = raceForSimilarity.positions().last().entries
        .sortedBy { it.value.first }
        .map { it.key }
        .chunked(10)

    // TODO Total hindrance of horse A is the summation of max(0, 1-e^(-x)) where x is the distance of
    // TODO every competitor from horse A. Will have to divide this by some number (maybe competitorCount) to get
    // TODO within 0 to 1 range. Not every horse will hinder the racer, so some horses are randomly excluded
    // TODO from the previously described calculation.

////    val race = Race(trackPool.random(random), competitorPool.sample(2..competitorPool.size))
    val race = Race(trackPool[0], competitorPoolBySimilarity[1])

//    val exchange = Exchange(bettorPool.sample(2..bettorPool.size))
    val exchange = Market(bettorPool.take(1))

    println("Track(length=${race.track.length.value})")
    println("Competitors(size=${race.competitors.size})")
    println()

    race.competitors.forEach { println("$it ${it.variability}") }
    println()

    race.positions().forEachIndexed { tick, positions ->
        println("tick=$tick positionsSize=${positions.size}")
        positions.forEach { entry ->
            val aheadCompetitors = positions
                .filter { it != entry }
                .filterValues { pair -> pair.first <= entry.value.first }

            val hinderingCompetitors = aheadCompetitors.entries.sample(aheadCompetitors.entries.indices).toMap()

            val g = hinderingCompetitors.values
                .map { (it.second - entry.value.second).value }
                .fold(1.0) { acc, distance -> acc - (E.pow(-distance) / race.competitors.size) }

            println("$entry ahead=${aheadCompetitors.keys.map { it.index }} hindering=${hinderingCompetitors.map { it.key.index }} g=$g")
        }

//        val a = (0 until 5)
//            .toFlowable()
//            .subscribeOn(Schedulers.computation())
//            .observeOn(Schedulers.computation())
//            .map { race.positions().last().mapValues { it.value.first }.maxBy { (_, position) -> position }!!.key }
//            .toList()
//            .subscribe { dryRunWinners -> println(dryRunWinners.countBy()) }
//
//        while (!a.isDisposed) {
//        }

        println(
            (0 until 100)
                .map { race.positions(positions).last().mapValues { it.value.first }.minBy { (_, position) -> position }!!.key }
                .countBy()
        )

        println()
    }
}
