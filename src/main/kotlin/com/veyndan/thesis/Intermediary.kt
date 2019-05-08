package com.veyndan.thesis

import com.veyndan.thesis.math.random
import com.veyndan.thesis.race.Competitor
import com.veyndan.thesis.race.Race
import com.veyndan.thesis.race.Track
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers
import org.nield.kotlinstatistics.countBy
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

const val path = "cache"

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

    fun item(): T

    fun read(): T {
        val file = File(pathname)

        if (!file.exists()) {
            println("Caching…")
            val item = item()
            cache(item)
            return item
        } else {
            file
                .inputStream()
                .use { fileInputStream ->
                    ObjectInputStream(fileInputStream).use { objectInputStream ->
                        @Suppress("UNCHECKED_CAST")
                        return objectInputStream.readObject() as T
                    }
                }
        }
    }
}

class RacePool(
    private val competitorsSize: Int,
    private val tracksSize: Int,
    private val factorCount: Int,
    private val simulations: Int,
    private val competitorVariability: () -> ClosedFloatingPointRange<Double>,
    private val competitorChunking: () -> Int
) : Cacheable<List<Race>> {

    override val pathname = "$path/racePool_factorCount_${factorCount}_competitorsSize_${competitorsSize}_tracksSize_${tracksSize}_simulations_$simulations"

    override fun item(): List<Race> {
        val trackPool = Track.generate(factorCount).take(tracksSize)
        val competitorPool = Competitor.generate(factorCount, competitorVariability).take(competitorsSize).toList()

        return trackPool
            .flatMap { track ->
                println(track)
                generateSequence { competitorPool.shuffled(random) }
                    .take(simulations)
                    .toFlowable()
                    .parallel()
                    .runOn(Schedulers.computation())
                    .flatMap { competitorPoolRandomization -> competitorPoolRandomization.chunked(5).toFlowable() }
                    .map { competitorPoolSample -> Race(track, competitorPoolSample) }
                    .map { race -> race.positions().last().minBy { it.value.first }!!.key }
                    .sequential()
                    .blockingIterable()
                    .countBy()
                    .entries
                    .groupBy(keySelector = { it.value }, valueTransform = { it.key })
                    .values
                    .flatten()
                    .windowed(5, 5)
                    .map { competitors -> Race(track, competitors) }
                    .asSequence()
            }
            .toList()
    }
}
