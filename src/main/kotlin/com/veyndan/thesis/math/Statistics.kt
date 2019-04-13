package com.veyndan.thesis.math

import com.veyndan.thesis.race.Distance
import org.apache.commons.math3.stat.descriptive.SummaryStatistics

fun Sequence<Distance>.mean(): Distance {
    val summaryStatistics = SummaryStatistics()
    forEach { summaryStatistics.addValue(it.value) }
    return Distance(summaryStatistics.mean)
}

fun Iterable<Distance>.mean() = asSequence().mean()
