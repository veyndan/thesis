@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis.exchange

import com.veyndan.thesis.Pennies
import com.veyndan.thesis.math.NATURAL_NUMBERS
import com.veyndan.thesis.math.random
import com.veyndan.thesis.random

data class Bettor(val id: Id, var funds: Pennies) {

    data class Id(val value: ULong)

    companion object {

        fun generate(fundsRange: ClosedRange<Pennies>) = NATURAL_NUMBERS
            .map { i -> Bettor(id = Bettor.Id(i), funds = fundsRange.random(random)) }
    }
}
