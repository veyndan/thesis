@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis.exchange

import com.veyndan.thesis.exchange.Exchange.Companion.BETTING_LIMIT
import com.veyndan.thesis.math.NATURAL_NUMBERS
import com.veyndan.thesis.math.random

/**
 * @property funds In pennies.
 */
data class Bettor(val id: ULong, val funds: ULong) {

    init {
        require(funds in BETTING_LIMIT)
    }

    companion object {

        fun generate(fundsRange: ULongRange) = NATURAL_NUMBERS
            .map { i -> Bettor(id = i, funds = fundsRange.random(random)) }
    }
}
