package com.veyndan.thesis.race

import com.veyndan.thesis.Bound

data class Track(val length: Distance, val factors: List<Factor>) {

    data class Factor(override val value: Double) : Bound(value, 0.0..1.0)
}
