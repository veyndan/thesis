package com.veyndan.thesis.exchange

import com.veyndan.thesis.remove
import com.veyndan.thesis.set

class OrderBook {

    val backs = mapOf<Odds, MutableList<Order.Back>>().toSortedMap(Odds.COMPARATOR_BACK)
    val lays = mapOf<Odds, MutableList<Order.Lay>>().toSortedMap(Odds.COMPARATOR_LAY)

    operator fun plusAssign(order: Order) {
        val potentialMatch = when (order) {
            is Order.Back -> lays.values.firstOrNull()?.first()
            is Order.Lay -> backs.values.firstOrNull()?.first()
        }

        if (potentialMatch != null && potentialMatch.matches(order)) { // Market order
            when {
                order.stake == potentialMatch.stake -> {
                    when (order) {
                        is Order.Back -> lays.remove((potentialMatch as Order.Lay).odds, potentialMatch)
                        is Order.Lay -> backs.remove((potentialMatch as Order.Back).odds, potentialMatch)
                    }
                }
                order.stake < potentialMatch.stake -> {
                    when (order) {
                        is Order.Back -> lays.getValue(potentialMatch.odds)[0] =
                            (potentialMatch as Order.Lay).copy(stake = potentialMatch.stake - order.stake)
                        is Order.Lay -> backs.getValue(potentialMatch.odds)[0] =
                            (potentialMatch as Order.Back).copy(stake = potentialMatch.stake - order.stake)
                    }
                }
                order.stake > potentialMatch.stake -> {
                    when (order) {
                        is Order.Back -> {
                            lays.remove((potentialMatch as Order.Lay).odds, potentialMatch)
                            plusAssign(order.copy(stake = order.stake - potentialMatch.stake))
                        }
                        is Order.Lay -> {
                            backs.remove((potentialMatch as Order.Back).odds, potentialMatch)
                            plusAssign(order.copy(stake = order.stake - potentialMatch.stake))
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

    operator fun minusAssign(order: Order) {
        when (order) {
            is Order.Back -> backs.remove(order.odds, order)
            is Order.Lay -> lays.remove(order.odds, order)
        }
    }
}
