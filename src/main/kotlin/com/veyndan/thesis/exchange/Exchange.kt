package com.veyndan.thesis.exchange

import com.veyndan.thesis.remove
import com.veyndan.thesis.set
import java.math.BigDecimal

inline class Price(val value: BigDecimal)

inline class Trader(val id: Long)

sealed class Odds {

    abstract val value: BigDecimal

    data class Back(override val value: BigDecimal) : Odds(), Comparable<Back> {

        fun matches(lay: Odds.Lay) = value <= lay.value

        override fun compareTo(other: Back): Int = value.compareTo(other.value)
    }

    data class Lay(override val value: BigDecimal) : Odds(), Comparable<Lay> {

        fun matches(back: Odds.Back) = value >= back.value

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
        when (order) {
            is Order.Back -> {
                val bestLay = lays.values.firstOrNull()?.first() ?: Order.Lay.UNMATCHABLE

                if (bestLay.odds.matches(order.odds)) {
                    if (order.price.value == bestLay.price.value) {
                        lays.remove(bestLay.odds, bestLay)
                    } else if (order.price.value < bestLay.price.value) {
                        lays.getValue(bestLay.odds)[0] = bestLay.copy(price = Price(bestLay.price.value - order.price.value))
                    } else {
                        error("Don't know what to do")
                    }
                } else {
                    backs[order.odds] = order
                }
            }
            is Order.Lay -> {
                val bestBack = backs.values.firstOrNull()?.first() ?: Order.Back.UNMATCHABLE

                if (bestBack.odds.matches(order.odds)) {
                    backs.remove(bestBack.odds, bestBack)
                } else {
                    lays[order.odds] = order
                }
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
