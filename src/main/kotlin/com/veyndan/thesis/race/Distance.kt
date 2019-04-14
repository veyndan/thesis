package com.veyndan.thesis.race

import org.nield.kotlinstatistics.averageBy

inline class Distance(val value: Double) {

    operator fun plus(other: Distance) = Distance(value + other.value)

    operator fun minus(other: Distance) = Distance(value - other.value)

    operator fun div(other: Distance) = Distance(value / other.value)
}

fun Iterable<Distance>.sum() = Distance(sumByDouble { it.value })

fun Sequence<Map<Competitor, Distance>>.averageBy(): Map<Competitor, Distance> = flatMap { it.entries.asSequence() }
    .map { (key, value) -> key to value.value }
    .averageBy()
    .mapValues { (_, value) -> Distance(value) }
