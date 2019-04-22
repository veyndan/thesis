@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis.exchange

import com.veyndan.thesis.Pennies
import com.veyndan.thesis.toPennies
import com.veyndan.thesis.toPounds

sealed class Order {

    abstract val bettorId: Bettor.Id
    abstract val stake: Pennies
    abstract val liability: Pennies
    abstract val odds: Odds

    protected fun validate() {
        require(stake in STAKE_LIMIT) { "Stake must be in range $STAKE_LIMIT but was $stake" }
        require(odds in ODDS_LIMIT) { "Odds must be in range $ODDS_LIMIT but was $odds}" }

        when (odds) {
            in 1.toOdds()..2.toOdds() -> require(odds % 0.01.toOdds() == 0.toOdds())
            in 2.toOdds()..3.toOdds() -> require(odds % 0.02.toOdds() == 0.toOdds())
            in 3.toOdds()..4.toOdds() -> require(odds % 0.05.toOdds() == 0.toOdds())
            in 4.toOdds()..6.toOdds() -> require(odds % 0.1.toOdds() == 0.toOdds())
            in 6.toOdds()..10.toOdds() -> require(odds % 0.2.toOdds() == 0.toOdds())
            in 10.toOdds()..20.toOdds() -> require(odds % 0.5.toOdds() == 0.toOdds())
            in 20.toOdds()..30.toOdds() -> require(odds % 1.toOdds() == 0.toOdds())
            in 30.toOdds()..50.toOdds() -> require(odds % 2.toOdds() == 0.toOdds())
            in 50.toOdds()..100.toOdds() -> require(odds % 5.toOdds() == 0.toOdds())
            in 100.toOdds()..1000.toOdds() -> require(odds % 10.toOdds() == 0.toOdds())
        }
    }

    fun matches(other: Order) = when (this) {
        is Back -> other is Lay && odds <= other.odds
        is Lay -> other is Back && odds >= other.odds
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

        val STAKE_LIMIT = 2.toPounds()..ULong.MAX_VALUE.toPennies()
        val ODDS_LIMIT = 1.01.toOdds()..1000.toOdds()
    }
}
