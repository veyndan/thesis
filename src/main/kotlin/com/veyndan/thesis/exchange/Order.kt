@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis.exchange

import java.math.BigDecimal

sealed class Order {

    abstract val trader: Bettor
    abstract val price: Price
    abstract val odds: Odds

    data class Back(
        override val trader: Bettor,
        override val price: Price,
        override val odds: Odds.Back
    ) : Order() {

        companion object {

            val UNMATCHABLE = Back(
                Bettor(id = ULong.MAX_VALUE, funds = ULong.MAX_VALUE),
                Price(BigDecimal.ZERO),
                Odds.Back(Double.MAX_VALUE.toBigDecimal())
            )
        }
    }

    data class Lay(
        override val trader: Bettor,
        override val price: Price,
        override val odds: Odds.Lay
    ) : Order() {

        companion object {

            val UNMATCHABLE = Lay(
                Bettor(id = ULong.MAX_VALUE, funds = ULong.MAX_VALUE),
                Price(BigDecimal.ZERO),
                Odds.Lay(Double.MIN_VALUE.toBigDecimal())
            )
        }
    }
}
