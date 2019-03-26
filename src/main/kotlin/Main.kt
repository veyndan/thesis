import org.apache.commons.math3.distribution.NormalDistribution
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.min

data class Competitor(val tmp: ClosedFloatingPointRange<Double>, val preferences: List<Competitor.Preference>) {

    private fun body(): Double =
        random.nextDouble(tmp.start, tmp.endInclusive)

    private fun compatibility(factors: List<Track.Factor>): Double =
        abs(1 - euclideanDistance(factors, preferences))

    fun stepSize(factors: List<Track.Factor>): Double =
        body() * compatibility(factors)

    data class Preference(override val value: Double) : Bound(value, 0.0..1.0)
}

data class Track(val length: Double, val factors: List<Track.Factor>) {

    data class Factor(override val value: Double) : Bound(value, 0.0..1.0)
}

data class Race(val track: Track, private val competitors: List<Competitor>) {

    fun positions(competitorsDistance: Map<Competitor, Double> = competitors.associateWith { 0.0 }): Sequence<Map<Competitor, Double>> =
        sequenceOf(competitorsDistance) + sequenceOf(competitorsDistance)
            .takeWhile { it.any { (_, distance) -> distance < track.length } }
            .flatMap {
                positions(it.mapValues { (competitor, distance) ->
                    min(distance + competitor.stepSize(track.factors), track.length)
                })
            }
}

fun competitors(factorCount: Int, rangeBounds: ClosedFloatingPointRange<Double>) = generateSequence {
    Competitor(
        random.nextDoubleRange(rangeBounds),
        List(factorCount) {
            Competitor.Preference(
                NormalDistribution(
                    random.nextDouble(0.0, 1.0),
                    random.nextDouble(0.0, 1.0)
                ).sample().coerceIn(0.01..1.0)
            )
        }
    )
}

fun main() {
    val factorCount = 1000

    val competitors = competitors(factorCount = factorCount, rangeBounds = 10.0..25.0).take(10)

    val competitorsSample = competitors.take(3).toList()
//    val competitorsSample = competitors.shuffled().take(random.nextInt(2, competitors.size))

    val race = Race(
        Track(500.0, List(factorCount) { Track.Factor(random.nextDouble(0.0, 1.0)) }),
        competitorsSample
    )

    println("TRACK")
    println(race.track)
    println()

    println("COMPETITORS")
    competitorsSample.forEach(::println)
    println()

    println()

    race.positions().forEachIndexed { tick, positions ->
        print("\rtick=$tick ${positions.map { (_, position) -> "$position" }.joinToString(" ")}")

        TimeUnit.MILLISECONDS.sleep(500L)
    }
}
