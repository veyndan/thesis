@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis

import com.veyndan.thesis.exchange.Bettor
import com.veyndan.thesis.race.Competitor
import com.veyndan.thesis.race.Distance
import com.veyndan.thesis.race.Race
import com.veyndan.thesis.race.Track
import java.util.concurrent.TimeUnit

fun main() {
    val factorCount = 100

    val competitors = Competitor.generate(factorCount = factorCount, rangeBounds = 10.0..25.0).take(10).toList()
    val competitorsSample = competitors.sample(2..Int.MAX_VALUE)

    val race = Race(
        Track(
            Distance(500.0),
            List(factorCount) { Track.Factor(random.nextDouble(0.0, 1.0)) }),
        competitorsSample
    )

    val bettors = Bettor.generate(5.pounds..10.pounds).take(10).toList()
    val bettorsSample = bettors.sample(2..Int.MAX_VALUE)

    println("TRACK")
    println(race.track)
    println()

    println("COMPETITORS")
    competitorsSample.forEach(::println)
    println()

    race.positions().forEachIndexed { tick, positions ->
        print("\rtick=$tick ${positions.map { (_, position) -> "${position.value}" }.joinToString(" ")}")

        TimeUnit.MILLISECONDS.sleep(500L)
    }
}
