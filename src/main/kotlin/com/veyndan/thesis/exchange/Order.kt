@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis.exchange

import com.veyndan.thesis.Pennies
import com.veyndan.thesis.toPounds

sealed class Order {

    abstract val bettorId: Bettor.Id
    abstract val stake: Pennies
    abstract val liability: Pennies
    abstract val odds: Odds

    fun matches(other: Order) = when (this) {
        is Order.Back -> other is Order.Lay && odds <= other.odds
        is Order.Lay -> other is Order.Back && odds >= other.odds
    }

    data class Back(
        override val bettorId: Bettor.Id,
        override val stake: Pennies,
        override val odds: Odds
    ) : Order() {

        override val liability: Pennies = stake

        companion object {

            val UNMATCHABLE = Back(Bettor.Id(ULong.MAX_VALUE), 0.toPounds(), Odds(ULong.MAX_VALUE))
        }
    }

    data class Lay(
        override val bettorId: Bettor.Id,
        override val stake: Pennies,
        override val odds: Odds
    ) : Order() {

        override val liability: Pennies = stake * (odds - 1.toOdds())

        companion object {

            val UNMATCHABLE = Lay(Bettor.Id(ULong.MAX_VALUE), 0.toPounds(), Odds(ULong.MIN_VALUE))
        }
    }
}
