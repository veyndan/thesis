package com.veyndan.thesis.exchange

import com.veyndan.thesis.remove
import com.veyndan.thesis.set
import java.math.BigDecimal

inline class Price(val value: BigDecimal)

inline class Trader(val id: Long)

sealed class Odds {

    abstract val value: BigDecimal

    fun matches(other: Odds) = when (this) {
        is Odds.Back -> other is Odds.Lay && value <= other.value
        is Odds.Lay -> other is Odds.Back && value >= other.value
    }

    data class Back(override val value: BigDecimal) : Odds(), Comparable<Back> {

        override fun compareTo(other: Back): Int = value.compareTo(other.value)
    }

    data class Lay(override val value: BigDecimal) : Odds(), Comparable<Lay> {

        override fun compareTo(other: Lay): Int = other.value.compareTo(value)
    }
}

sealed class Order {

    abstract val trader: Trader
    abstract val price: Price
    abstract val odds: Odds

    data class Back(
        override val trader: Trader,
        override val price: Price,
        override val odds: Odds.Back
    ) : Order() {

        companion object {

            val UNMATCHABLE = Order.Back(Trader(-1), Price(BigDecimal.ZERO), Odds.Back(Double.MAX_VALUE.toBigDecimal()))
        }
    }

    data class Lay(
        override val trader: Trader,
        override val price: Price,
        override val odds: Odds.Lay
    ) : Order() {

        companion object {

            val UNMATCHABLE = Order.Lay(Trader(-1), Price(BigDecimal.ZERO), Odds.Lay(Double.MIN_VALUE.toBigDecimal()))
        }
    }
}

class Exchange {

    val backs = sortedMapOf<Odds.Back, MutableList<Order.Back>>()
    val lays = sortedMapOf<Odds.Lay, MutableList<Order.Lay>>()

    fun addOrder(order: Order) {
//        fun match(order: Order) {
//            val potentialMatch = when (order) {
//                is Order.Back -> lays.values.firstOrNull()?.first() ?: Order.Lay.UNMATCHABLE
//                is Order.Lay -> backs.values.firstOrNull()?.first() ?: Order.Back.UNMATCHABLE
//            }
//
//            if (potentialMatch.odds.matches(order.odds)) { // Market order
//                when {
//                    order.price.value == potentialMatch.price.value -> lays.remove(potentialMatch.odds, potentialMatch)
//                    order.price.value < potentialMatch.price.value -> lays.getValue(potentialMatch.odds)[0] =
//                        potentialMatch.copy(price = Price(potentialMatch.price.value - order.price.value))
//                    order.price.value > potentialMatch.price.value -> {
//                        lays.remove(potentialMatch.odds, potentialMatch)
//                        match(order.copy(price = Price(order.price.value - potentialMatch.price.value)))
//                    }
//                }
//            } else { // Limit order
//                backs[order.odds] = order
//            }
//        }

        when (order) {
            is Order.Back -> {
                fun match(order: Order.Back) {
                    val bestLay = lays.values.firstOrNull()?.first() ?: Order.Lay.UNMATCHABLE

                    if (bestLay.odds.matches(order.odds)) { // Market order
                        when {
                            order.price.value == bestLay.price.value -> lays.remove(bestLay.odds, bestLay)
                            order.price.value < bestLay.price.value -> lays.getValue(bestLay.odds)[0] =
                                bestLay.copy(price = Price(bestLay.price.value - order.price.value))
                            order.price.value > bestLay.price.value -> {
                                lays.remove(bestLay.odds, bestLay)
                                match(order.copy(price = Price(order.price.value - bestLay.price.value)))
                            }
                        }
                    } else { // Limit order
                        backs[order.odds] = order
                    }
                }

                match(order)
            }
            is Order.Lay -> {
                fun match(order: Order.Lay) {
                    val bestBack = backs.values.firstOrNull()?.first() ?: Order.Back.UNMATCHABLE

                    if (bestBack.odds.matches(order.odds)) { // Market order
                        when {
                            order.price.value == bestBack.price.value -> backs.remove(bestBack.odds, bestBack)
                            order.price.value < bestBack.price.value -> backs.getValue(bestBack.odds)[0] =
                                bestBack.copy(price = Price(bestBack.price.value - order.price.value))
                            order.price.value > bestBack.price.value -> {
                                backs.remove(bestBack.odds, bestBack)
                                match(order.copy(price = Price(order.price.value - bestBack.price.value)))
                            }
                        }
                    } else { // Limit order
                        lays[order.odds] = order
                    }
                }

                match(order)
            }
        }
    }

    fun deleteOrder(order: Order) {
        when (order) {
            is Order.Back -> backs.remove(order.odds, order)
            is Order.Lay -> lays.remove(order.odds, order)
        }
    }
}
