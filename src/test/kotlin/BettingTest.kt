@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

import com.veyndan.thesis.exchange.Bettor
import com.veyndan.thesis.exchange.Exchange
import com.veyndan.thesis.race.Competitor
import com.veyndan.thesis.race.Distance
import com.veyndan.thesis.race.Race
import com.veyndan.thesis.race.Track
import com.veyndan.thesis.random
import org.junit.jupiter.api.Test

class BettingTest {

    @Test
    fun `One bettor betting randomly on a horse out of one possible horse`() {
        val race = run {
            val factorCount = 100
            Race(
                Track(Distance(1000.0), List(factorCount) { Track.Factor(random.nextDouble(0.0, 1.0)) }),
                Competitor.generate(factorCount, rangeBounds = 10.0..25.0).take(10).toList()
            )
        }
        val exchange = Exchange()
        val bettor = Bettor(100UL)

        race.positions().forEachIndexed { tick, positions ->
            if (tick == 0) {
                println(positions)
            }
        }
    }
}
