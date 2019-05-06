package com.veyndan.thesis.race

import com.veyndan.thesis.math.random
import java.io.Serializable

data class Track(val length: Distance, val factors: List<Factor>) : Serializable {

    data class Factor(val value: Double) : Serializable

    companion object {

        fun generator(factorCount: Int): (index: Int) -> Track = {
            Track(
                Distance(random.nextInt(402, 4828).toDouble()), // https://en.wikipedia.org/wiki/Flat_racing
                List(factorCount) { Factor(random.nextDouble(0.0, 1.0)) })
        }
    }
}
