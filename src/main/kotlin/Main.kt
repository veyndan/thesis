
import org.apache.commons.math3.distribution.NormalDistribution
import java.util.concurrent.TimeUnit
import kotlin.math.min

data class Competitor(val name: String, val tmp: ClosedFloatingPointRange<Double>, val preferences: List<Competitor.Preference>) {

    private fun body(): Double =
        random.nextDouble(tmp.start, tmp.endInclusive).also(::println)

    private fun compatibility(factors: List<Track.Factor>): Double =
        1 - euclideanDistance(factors, preferences)

    fun stepSize(factors: List<Track.Factor>): Double =
        body() * compatibility(factors)

    class Preference(value: Double) : Bound(value, 0.0..1.0)
}

data class Track(val length: Double, val factors: List<Track.Factor>) {

    class Factor(value: Double) : Bound(value, 0.0..1.0)
}

class Race(private val track: Track, private val competitors: Set<Competitor>) {

    fun positions(competitorsDistance: Map<Competitor, Double> = competitors.associateWith { 0.0 }): Sequence<Map<Competitor, Double>> =
        sequenceOf(competitorsDistance) + sequenceOf(competitorsDistance)
            .takeWhile { it.any { (_, distance) -> distance < track.length } }
            .flatMap {
                positions(it.mapValues { (competitor, distance) ->
                    min(distance + competitor.stepSize(track.factors), track.length)
                })
            }
}

fun main() {
    val competitors = setOf(
        Competitor(
            "A",
            10.0..20.0,
            listOf(
                Competitor.Preference(NormalDistribution(0.3, 0.5).sample().coerceIn(0.0..1.0)),
                Competitor.Preference(NormalDistribution(0.1, 0.5).sample().coerceIn(0.0..1.0))
            )
        ),
        Competitor(
            "B",
            1.0..25.0,
            listOf(
                Competitor.Preference(NormalDistribution(0.95, 0.5).sample().coerceIn(0.0..1.0)),
                Competitor.Preference(NormalDistribution(0.6, 0.5).sample().coerceIn(0.0..1.0))
            )
        )
    )

    val race = Race(
        Track(
            500.0,
            listOf(
                Track.Factor(0.7),
                Track.Factor(0.2)
            )
        ),
//        competitors.shuffled().take(random.nextInt(2, competitors.size)).toSet()
        competitors
    )

    race.positions().forEachIndexed { tick, positions ->
//        print("\rtick=$tick ${positions.map { (competitor, position) -> "${competitor.name}=$position" }.joinToString(" ")}")

        TimeUnit.MILLISECONDS.sleep(500L)
    }
}
