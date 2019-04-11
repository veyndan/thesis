@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis.exchange

import com.veyndan.thesis.InsufficientFundsException
import com.veyndan.thesis.remove
import com.veyndan.thesis.set
import com.veyndan.thesis.toPounds

class Exchange(val bettors: List<Bettor>) {

    val backs = mapOf<Odds, MutableList<Order.Back>>().toSortedMap(Odds.COMPARATOR_BACK)
    val lays = mapOf<Odds, MutableList<Order.Lay>>().toSortedMap(Odds.COMPARATOR_LAY)

    init {
        assert(bettors == bettors.distinctBy { it.id }) { "Each bettor must have a unique ID" }
    }

    fun addOrder(order: Order) {
        if (order.liability > bettors.first { it.id == order.bettorId }.funds) {
            throw InsufficientFundsException(order.liability, bettors.first { it.id == order.bettorId }.funds)
        }

        val potentialMatch = when (order) {
            is Order.Back -> lays.values.firstOrNull()?.first() ?: Order.Lay.UNMATCHABLE
            is Order.Lay -> backs.values.firstOrNull()?.first() ?: Order.Back.UNMATCHABLE
        }

        if (potentialMatch.matches(order)) { // Market order
            when {
                order.stake == potentialMatch.stake -> {
                    when (order) {
                        is Order.Back -> lays.remove((potentialMatch as Order.Lay).odds, potentialMatch)
                        is Order.Lay -> backs.remove((potentialMatch as Order.Back).odds, potentialMatch)
                    }
                }
                order.stake < potentialMatch.stake -> {
                    when (order) {
                        is Order.Back -> lays.getValue(potentialMatch.odds)[0] = (potentialMatch as Order.Lay).copy(
                            stake = potentialMatch.stake - order.stake
                        )
                        is Order.Lay -> backs.getValue(potentialMatch.odds)[0] = (potentialMatch as Order.Back).copy(
                            stake = potentialMatch.stake - order.stake
                        )
                    }
                }
                order.stake > potentialMatch.stake -> {
                    when (order) {
                        is Order.Back -> {
                            lays.remove((potentialMatch as Order.Lay).odds, potentialMatch)
                            addOrder(order.copy(stake = order.stake - potentialMatch.stake))
                        }
                        is Order.Lay -> {
                            backs.remove((potentialMatch as Order.Back).odds, potentialMatch)
                            addOrder(order.copy(stake = order.stake - potentialMatch.stake))
                        }
                    }
                }
            }
        } else { // Limit order
            bettors.first { it.id == order.bettorId }.funds -= order.liability

            when (order) {
                is Order.Back -> backs[order.odds] = order
                is Order.Lay -> lays[order.odds] = order
            }
        }
    }

    fun cancelOrder(order: Order) {
        bettors.first { it.id == order.bettorId }.funds += order.liability

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
