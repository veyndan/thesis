@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis.exchange

import com.veyndan.thesis.Pennies
import com.veyndan.thesis.toPennies

sealed class Order {

    abstract val bettor: Bettor
    abstract val stake: Pennies
    abstract val odds: Odds

    fun matches(other: Order) = when (this) {
        is Order.Back -> other is Order.Lay && odds.value <= other.odds.value
        is Order.Lay -> other is Order.Back && odds.value >= other.odds.value
    }

    data class Back(
        override val bettor: Bettor,
        override val stake: Pennies,
        override val odds: Odds
    ) : Order() {

        companion object {

            val UNMATCHABLE = Back(
                Bettor(id = ULong.MAX_VALUE, funds = ULong.MAX_VALUE.toPennies()),
                0.toPennies(),
                Odds(ULong.MAX_VALUE)
            )
        }
    }

    data class Lay(
        override val bettor: Bettor,
        override val stake: Pennies,
        override val odds: Odds
    ) : Order() {

        companion object {

            val UNMATCHABLE = Lay(
                Bettor(id = ULong.MAX_VALUE, funds = ULong.MAX_VALUE.toPennies()),
                0.toPennies(),
                Odds(ULong.MIN_VALUE)
            )
        }
    }
}
