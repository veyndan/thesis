@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis.exchange

import com.veyndan.thesis.Pennies
import com.veyndan.thesis.math.NATURAL_NUMBERS
import com.veyndan.thesis.math.random
import com.veyndan.thesis.toPennies

data class Bettor(val id: Id, var funds: Pennies) {

    data class Id(val value: ULong)

    // TODO There shouldn't be a restriction on funds, only size of bet
//    init {
//        require(funds.value in BETTING_LIMIT) { "Funds (${funds.value}) not in allowed range ($BETTING_LIMIT)"}
//    }

    companion object {

        fun generate(fundsRange: ULongRange) = NATURAL_NUMBERS
            .map { i -> Bettor(id = Bettor.Id(i), funds = fundsRange.random(random).toPennies()) }
    }
}
