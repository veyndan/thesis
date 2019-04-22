@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis.exchange

import com.veyndan.thesis.Pennies
import com.veyndan.thesis.math.random
import com.veyndan.thesis.random

data class Bettor(val id: Id, var funds: Pennies, val dryRunCount: UInt) {

    data class Id(val value: ULong)

    companion object {

        fun generator(fundsRange: ClosedRange<Pennies>, dryRunsRange: UIntRange): (index: Int) -> Bettor = { index ->
            Bettor(
                id = Id(index.toULong()),
                funds = fundsRange.random(random),
                dryRunCount = dryRunsRange.random(random)
            )
        }
    }
}
