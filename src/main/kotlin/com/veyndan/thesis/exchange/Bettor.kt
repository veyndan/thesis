@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis.exchange

import com.veyndan.thesis.exchange.Exchange.Companion.BETTING_LIMIT
import com.veyndan.thesis.random

/**
 * @property funds In pennies.
 */
data class Bettor(val funds: ULong) {

    init {
        require(funds in BETTING_LIMIT)
    }

    companion object {

        fun generate(fundsRange: ULongRange) = generateSequence { Bettor(fundsRange.random(random)) }
    }
}
