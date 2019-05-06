package com.veyndan.thesis

import com.veyndan.thesis.math.nextDoubleRange
import com.veyndan.thesis.math.random
import com.veyndan.thesis.race.Competitor
import com.veyndan.thesis.race.Race
import com.veyndan.thesis.race.Track
import com.veyndan.thesis.utility.chunked
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

val path = "cache"

interface Cacheable<T> {

    val pathname: String

    private fun cache(item: T) {
        File(pathname)
            .outputStream()
            .use { fileOutputStream ->
                ObjectOutputStream(fileOutputStream)
                    .use { objectOutputStream ->
                        objectOutputStream.writeObject(item)
                    }
            }
    }

    val item: T

    fun read(): T {
        val file = File(pathname)

        if (!file.exists()) {
            cache(item)
        }

        file
            .inputStream()
            .use { fileInputStream ->
                ObjectInputStream(fileInputStream).use { objectInputStream ->
                    return objectInputStream.readObject() as T
                }
            }
    }
}

private class CompetitorPool(factorCount: Int, size: Int) : Cacheable<List<Competitor>> {

    override val pathname = "$path/competitors_factorCount_${factorCount}_size_$size"

    override val item = List(size, Competitor.generator(factorCount, variability = { random.nextDoubleRange(9.9..10.0) }))
}

private class TrackPool(factorCount: Int, size: Int) : Cacheable<List<Track>> {

    override val pathname = "$path/track_factorCount_${factorCount}_size_$size"

    override val item = List(size, Track.generator(factorCount))
}

class RacePool(
    competitorsSize: Int,
    tracksSize: Int,
    factorCount: Int,
    competitorChunking: () -> Int
) : Cacheable<List<Race>> {

    override val pathname = "$path/racePool_factorCount_${factorCount}_competitorsSize_${competitorsSize}_tracksSize_$tracksSize"

    override val item = run {
        val trackPool = TrackPool(factorCount, tracksSize).read()
        val competitorPool = CompetitorPool(factorCount, size = competitorsSize).read()

        listOf(
            Race(
                trackPool[0],
                Race(trackPool[0], competitorPool)
                    .positions()
                    .last()
                    .entries
                    .sortedBy { it.value.first }
                    .map { it.key }
                    .chunked(competitorChunking)[1]
            )
        )
    }
}

fun main() {
    val factorCount = 100

    println(CompetitorPool(factorCount, size = 500).read())
    println(TrackPool(factorCount, size = 10).read())
}
