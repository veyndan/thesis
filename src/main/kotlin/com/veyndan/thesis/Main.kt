@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis

import com.veyndan.thesis.exchange.Bettor
import com.veyndan.thesis.exchange.Exchange
import com.veyndan.thesis.math.random
import com.veyndan.thesis.math.sample
import com.veyndan.thesis.race.Competitor
import com.veyndan.thesis.race.Race
import com.veyndan.thesis.race.Track
import java.util.concurrent.TimeUnit

fun main() {
    val factorCount = 100

    val competitorPool = List(10, Competitor.generator(factorCount, rangeBounds = 10.0..25.0))
    val trackPool = List(10, Track.generator(factorCount))
    val bettorPool = List(10, Bettor.generator(fundsRange = 5.toPounds()..10.toPounds()))

    val race = Race(trackPool.random(random), competitorPool.sample(2..Int.MAX_VALUE))

    val exchange = Exchange(bettorPool.sample(2..2))

    println("TRACK")
    println(race.track)
    println()

    println("COMPETITORS")
    race.competitors.forEach(::println)
    println()

    race.positions().forEachIndexed { tick, positions ->
        print("\rtick=$tick ${positions.map { (_, position) -> "${position.value}" }.joinToString(" ")}")

        TimeUnit.MILLISECONDS.sleep(500L)
    }
}
