@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis.exchange

import java.math.BigDecimal

sealed class Order {

    abstract val trader: Trader
    abstract val price: Price
    abstract val odds: Odds

    data class Back(
        override val trader: Trader,
        override val price: Price,
        override val odds: Odds.Back
    ) : Order() {

        companion object {

            val UNMATCHABLE = Back(
                Trader(ULong.MAX_VALUE),
                Price(BigDecimal.ZERO),
                Odds.Back(Double.MAX_VALUE.toBigDecimal())
            )
        }
    }

    data class Lay(
        override val trader: Trader,
        override val price: Price,
        override val odds: Odds.Lay
    ) : Order() {

        companion object {

            val UNMATCHABLE = Lay(
                Trader(ULong.MAX_VALUE),
                Price(BigDecimal.ZERO),
                Odds.Lay(Double.MIN_VALUE.toBigDecimal())
            )
        }
    }
}
