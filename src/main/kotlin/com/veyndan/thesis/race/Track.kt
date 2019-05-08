@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis.race

import com.veyndan.thesis.math.NATURAL_NUMBERS
import com.veyndan.thesis.math.random
import java.io.Serializable

data class Track(val index: UInt, val length: Distance, val factors: List<Factor>) : Serializable {

    override fun toString() = "Track($index, length=$length)"

    data class Factor(val value: Double) : Serializable

    companion object {

        fun generate(factorCount: Int) = NATURAL_NUMBERS.map(generator(factorCount))

        private fun generator(factorCount: Int): (index: UInt) -> Track = { index: UInt ->
            Track(
                index,
                Distance(random.nextInt(402, 4828).toDouble()), // https://en.wikipedia.org/wiki/Flat_racing
                List(factorCount) { Factor(random.nextDouble(0.0, 1.0)) })
        }
    }
}
