package com.veyndan.thesis.exchange

import com.veyndan.thesis.math.Bound

data class Probability(override val value: Double) : Bound(value, 0.0..1.0)
