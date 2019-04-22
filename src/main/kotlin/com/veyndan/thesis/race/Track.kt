package com.veyndan.thesis.race

import com.veyndan.thesis.math.Bound
import com.veyndan.thesis.math.random

data class Track(val length: Distance, val factors: List<Factor>) {

    data class Factor(override val value: Double) : Bound(value, 0.0..1.0)

    companion object {

        fun generator(factorCount: Int): (index: Int) -> Track = {
            Track(
                Distance(random.nextInt(402, 4828).toDouble()), // https://en.wikipedia.org/wiki/Flat_racing
                List(factorCount) { Factor(random.nextDouble(0.0, 1.0)) })
        }
    }
}
