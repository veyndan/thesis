@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis.exchange

import com.veyndan.thesis.Pennies
import com.veyndan.thesis.toPounds

sealed class Order {

    abstract val bettorId: Bettor.Id
    abstract val stake: Pennies
    abstract val liability: Pennies
    abstract val odds: Odds

    protected fun validate() {
        require(stake.value in STAKE_LIMIT) { "Stake must be in range $STAKE_LIMIT but was $stake" }
        require(odds.value in ODDS_LIMIT) { "Odds must be in range $ODDS_LIMIT but was $odds}" }

        when (odds.value) {
            in 1.toOdds().value..2.toOdds().value -> require(odds % 0.01.toOdds() == 0.toOdds())
            in 2.toOdds().value..3.toOdds().value -> require(odds % 0.02.toOdds() == 0.toOdds())
            in 3.toOdds().value..4.toOdds().value -> require(odds % 0.05.toOdds() == 0.toOdds())
            in 4.toOdds().value..6.toOdds().value -> require(odds % 0.1.toOdds() == 0.toOdds())
            in 6.toOdds().value..10.toOdds().value -> require(odds % 0.2.toOdds() == 0.toOdds())
            in 10.toOdds().value..20.toOdds().value -> require(odds % 0.5.toOdds() == 0.toOdds())
            in 20.toOdds().value..30.toOdds().value -> require(odds % 1.toOdds() == 0.toOdds())
            in 30.toOdds().value..50.toOdds().value -> require(odds % 2.toOdds() == 0.toOdds())
            in 50.toOdds().value..100.toOdds().value -> require(odds % 5.toOdds() == 0.toOdds())
            in 100.toOdds().value..1000.toOdds().value -> require(odds % 10.toOdds() == 0.toOdds())
        }
    }

    fun matches(other: Order) = when (this) {
        is Order.Back -> other is Order.Lay && odds <= other.odds
        is Order.Lay -> other is Order.Back && odds >= other.odds
    }

    data class Back(
        override val bettorId: Bettor.Id,
        override val stake: Pennies,
        override val odds: Odds
    ) : Order() {

        init {
            validate()
        }

        override val liability: Pennies = stake
    }

    data class Lay(
        override val bettorId: Bettor.Id,
        override val stake: Pennies,
        override val odds: Odds
    ) : Order() {

        init {
            validate()
        }

        override val liability: Pennies = stake * (odds - 1.toOdds())
    }

    companion object {

        val STAKE_LIMIT = 2.toPounds().value..ULong.MAX_VALUE
        val ODDS_LIMIT = 1.01.toOdds().value..1000.toOdds().value
    }
}
