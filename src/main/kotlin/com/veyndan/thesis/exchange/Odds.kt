package com.veyndan.thesis.exchange

import java.math.BigDecimal

sealed class Odds {

    abstract val value: BigDecimal

    fun matches(other: Odds) = when (this) {
        is Back -> other is Lay && value <= other.value
        is Lay -> other is Back && value >= other.value
    }

    data class Back(override val value: BigDecimal) : Odds(), Comparable<Back> {

        override fun compareTo(other: Back): Int = value.compareTo(other.value)
    }

    data class Lay(override val value: BigDecimal) : Odds(), Comparable<Lay> {

        override fun compareTo(other: Lay): Int = other.value.compareTo(value)
    }
}
