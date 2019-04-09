@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis.exchange

import com.veyndan.thesis.pounds
import com.veyndan.thesis.remove
import com.veyndan.thesis.set

class Exchange {

    val backs = sortedMapOf<Odds.Back, MutableList<Order.Back>>()
    val lays = sortedMapOf<Odds.Lay, MutableList<Order.Lay>>()

    fun addOrder(order: Order) {
        val potentialMatch = when (order) {
            is Order.Back -> lays.values.firstOrNull()?.first() ?: Order.Lay.UNMATCHABLE
            is Order.Lay -> backs.values.firstOrNull()?.first() ?: Order.Back.UNMATCHABLE
        }

        if (potentialMatch.odds.matches(order.odds)) { // Market order
            when {
                order.price.value == potentialMatch.price.value -> {
                    when (order) {
                        is Order.Back -> lays.remove((potentialMatch as Order.Lay).odds, potentialMatch)
                        is Order.Lay -> backs.remove((potentialMatch as Order.Back).odds, potentialMatch)
                    }
                }
                order.price.value < potentialMatch.price.value -> {
                    when (order) {
                        is Order.Back -> lays.getValue((potentialMatch as Order.Lay).odds)[0] =
                            potentialMatch.copy(price = Price(potentialMatch.price.value - order.price.value))
                        is Order.Lay -> backs.getValue((potentialMatch as Order.Back).odds)[0] =
                            potentialMatch.copy(price = Price(potentialMatch.price.value - order.price.value))
                    }
                }
                order.price.value > potentialMatch.price.value -> {
                    when (order) {
                        is Order.Back -> {
                            lays.remove((potentialMatch as Order.Lay).odds, potentialMatch)
                            addOrder(order.copy(price = Price(order.price.value - potentialMatch.price.value)))
                        }
                        is Order.Lay -> {
                            backs.remove((potentialMatch as Order.Back).odds, potentialMatch)
                            addOrder(order.copy(price = Price(order.price.value - potentialMatch.price.value)))
                        }
                    }
                }
            }
        } else { // Limit order
            when (order) {
                is Order.Back -> backs[order.odds] = order
                is Order.Lay -> lays[order.odds] = order
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
        val BETTING_LIMIT = 2.pounds.value..ULong.MAX_VALUE
    }
}
