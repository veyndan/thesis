package com.veyndan.thesis.race

import org.nield.kotlinstatistics.Descriptives
import org.nield.kotlinstatistics.averageBy
import org.nield.kotlinstatistics.descriptiveStatisticsBy

fun min(a: Distance, b: Distance) = Distance(Math.min(a.value, b.value))

inline class Distance(val value: Double) : Comparable<Distance> {

    operator fun plus(other: Distance) = Distance(value + other.value)

    operator fun minus(other: Distance) = Distance(value - other.value)

    // The units cancel each other out when dividing, e.g. 4km/2km=2.
    operator fun div(other: Distance) = value / other.value

    operator fun div(other: Double) = Distance(value / other)

    override fun compareTo(other: Distance) = value.compareTo(other.value)

    override fun toString() = "Distance($value)"
}

operator fun Int.times(other: Distance) = Distance(this * other.value)

fun Iterable<Distance>.sum() = Distance(sumByDouble { it.value })

fun Sequence<Map<Competitor, Distance>>.averageBy(): Map<Competitor, Distance> = flatMap { it.entries.asSequence() }
    .averageBy(keySelector = { it.key }, doubleSelector = { it.value.value })
    .mapValues { Distance(it.value) }

fun Sequence<Map<Competitor, Distance>>.descriptiveStatisticsBy(): Map<Competitor, Descriptives> =
    flatMap { it.entries.asSequence() }
        .descriptiveStatisticsBy(keySelector = { it.key }, valueSelector = { it.value.value })
        .mapValues { it.value }
