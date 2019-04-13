package com.veyndan.thesis.race

inline class Distance(val value: Double) {

    operator fun plus(other: Distance) = Distance(value + other.value)

    operator fun div(other: Distance) = Distance(value / other.value)
}

fun Iterable<Distance>.sum() = Distance(sumByDouble { it.value })
