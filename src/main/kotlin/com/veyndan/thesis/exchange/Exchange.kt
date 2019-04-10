@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis.exchange

import com.veyndan.thesis.remove
import com.veyndan.thesis.set
import com.veyndan.thesis.toPennies
import com.veyndan.thesis.toPounds

class Exchange {

    val backs = mapOf<Odds, MutableList<Order.Back>>().toSortedMap(Odds.COMPARATOR_BACK)
    val lays = mapOf<Odds, MutableList<Order.Lay>>().toSortedMap(Odds.COMPARATOR_LAY)

    fun addOrder(order: Order) {
        val potentialMatch = when (order) {
            is Order.Back -> lays.values.firstOrNull()?.first() ?: Order.Lay.UNMATCHABLE
            is Order.Lay -> backs.values.firstOrNull()?.first() ?: Order.Back.UNMATCHABLE
        }

        if (potentialMatch.matches(order)) { // Market order
            when {
                order.stake.value == potentialMatch.stake.value -> {
                    when (order) {
                        is Order.Back -> lays.remove((potentialMatch as Order.Lay).odds, potentialMatch)
                        is Order.Lay -> backs.remove((potentialMatch as Order.Back).odds, potentialMatch)
                    }
                }
                order.stake.value < potentialMatch.stake.value -> {
                    when (order) {
                        is Order.Back -> lays.getValue((potentialMatch as Order.Lay).odds)[0] = potentialMatch.copy(
                            stake = (potentialMatch.stake.value - order.stake.value).toPennies()
                        )
                        is Order.Lay -> backs.getValue((potentialMatch as Order.Back).odds)[0] = potentialMatch.copy(
                            stake = (potentialMatch.stake.value - order.stake.value).toPennies()
                        )
                    }
                }
                order.stake.value > potentialMatch.stake.value -> {
                    when (order) {
                        is Order.Back -> {
                            lays.remove((potentialMatch as Order.Lay).odds, potentialMatch)
                            addOrder(order.copy(stake = (order.stake.value - potentialMatch.stake.value).toPennies()))
                        }
                        is Order.Lay -> {
                            backs.remove((potentialMatch as Order.Back).odds, potentialMatch)
                            addOrder(order.copy(stake = (order.stake.value - potentialMatch.stake.value).toPennies()))
                        }
                    }
                }
            }
        } else { // Limit order
            when (order) {
                is Order.Back -> backs[order.odds] = order
                // Test against this: https://www.betfair.com.au/hub/betfair-betting-simulator/
//                TODO is Order.Back -> backs[order.odds] = order.copy(bettor = order.bettor.copy(funds = (order.bettor.funds.value - order.stake.value).toPennies()))
                is Order.Lay -> lays[order.odds] = order
//                TODO is Order.Lay -> lays[order.odds] = order.copy(bettor = order.bettor.copy(funds = (order.bettor.funds.value - (order.stake.value * (order.odds.value - BigDecimal.ONE))).toPennies()))
            }
        }
    }

    fun deleteOrder(order: Order) {
        when (order) {
            is Order.Back -> backs.remove(order.odds, order)
            is Order.Lay -> lays.remove(order.odds, order)
        }
    }

    companion object {

        /**
         * Allowable stake range in pennies.
         */
        val BETTING_LIMIT = 2.toPounds().value..ULong.MAX_VALUE
    }
}
