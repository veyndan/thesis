@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis.exchange

import com.veyndan.thesis.Pennies
import com.veyndan.thesis.toPennies

sealed class Order {

    abstract val bettor: Bettor
    abstract val stake: Pennies
    abstract val odds: Odds

    data class Back(
        override val bettor: Bettor,
        override val stake: Pennies,
        override val odds: Odds.Back
    ) : Order() {

        companion object {

            val UNMATCHABLE = Back(
                Bettor(id = ULong.MAX_VALUE, funds = ULong.MAX_VALUE.toPennies()),
                0.toPennies(),
                Odds.Back(Double.MAX_VALUE.toBigDecimal())
            )
        }
    }

    data class Lay(
        override val bettor: Bettor,
        override val stake: Pennies,
        override val odds: Odds.Lay
    ) : Order() {

        companion object {

            val UNMATCHABLE = Lay(
                Bettor(id = ULong.MAX_VALUE, funds = ULong.MAX_VALUE.toPennies()),
                0.toPennies(),
                Odds.Lay(Double.MIN_VALUE.toBigDecimal())
            )
        }
    }
}
