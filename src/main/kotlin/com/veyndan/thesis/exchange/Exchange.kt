package com.veyndan.thesis.exchange

import com.squareup.sqldelight.runtime.rx.asObservable
import com.squareup.sqldelight.runtime.rx.mapToList
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import com.veyndan.thesis.Database
import com.veyndan.thesis.remove
import java.math.BigDecimal

data class Trader(val id: Long)

sealed class Odds {

    abstract val value: BigDecimal

    data class Back(override val value: BigDecimal) : Odds(), Comparable<Back> {

        override fun compareTo(other: Back): Int = value.compareTo(other.value)
    }

    data class Lay(override val value: BigDecimal) : Odds(), Comparable<Lay> {

        override fun compareTo(other: Lay): Int = other.value.compareTo(value)
    }
}

sealed class Order {

    abstract val trader: Trader
    abstract val odds: Odds

    data class Back(
        override val trader: Trader,
        override val odds: Odds.Back
    ) : Order()

    data class Lay(
        override val trader: Trader,
        override val odds: Odds.Lay
    ) : Order()
}

class Exchange {

    private val driver = JdbcSqliteDriver(name = "jdbc:sqlite:/Users/veyndan/IdeaProjects/thesis/src/main/thesis.db")
        .also { Database.Schema.create(it) }
    private val database = Database(driver)
    private val exchangeQueries = database.exchangeQueries

    val backs = sortedMapOf<Odds.Back, MutableList<Order.Back>>()
    val lays = sortedMapOf<Odds.Lay, MutableList<Order.Lay>>()

    init {
        events().subscribe { bet ->
            println("veyndan $bet")
//            when (bet.side) {
//                "Back" -> {
//                    if (lays.values.isEmpty()) {
//                        backs[Odds.Back(bet.odds.toBigDecimal())] =
//                            Order.Back(Trader(bet.trader), Odds.Back(bet.odds.toBigDecimal()))
//                    } else {
//                        val bestLay = lays.values.first()[0]
//                        if (bestLay.odds.value >= bet.odds.toBigDecimal()) {
//                            lays.remove(bestLay.odds, bestLay)
//                        } else {
//                            backs[Odds.Back(bet.odds.toBigDecimal())] =
//                                Order.Back(Trader(bet.trader), Odds.Back(bet.odds.toBigDecimal()))
//                        }
//                    }
//                }
//                "Lay" -> {
//                    if (backs.values.isEmpty()) {
//                        lays[Odds.Lay(bet.odds.toBigDecimal())] =
//                            Order.Lay(Trader(bet.trader), Odds.Lay(bet.odds.toBigDecimal()))
//                    } else {
//                        val bestBack = backs.values.first()[0]
//                        if (bestBack.odds.value <= bet.odds.toBigDecimal()) {
//                            backs.remove(bestBack.odds, bestBack)
//                        } else {
//                            lays[Odds.Lay(bet.odds.toBigDecimal())] =
//                                Order.Lay(Trader(bet.trader), Odds.Lay(bet.odds.toBigDecimal()))
//                        }
//                    }
//                }
//                else -> error("Huh?")
//            }
        }
    }

    fun events() = exchangeQueries.events()
        .asObservable()
        .mapToList()
        .skip(1) // First event contains no events
        .map {
            println(it)
            it.last()
        }

    fun addOrder(order: Order) {
        when (order) {
            is Order.Back -> {
                if (lays.values.isEmpty()) {
                    exchangeQueries.insert(
                        side = "Back",
                        operation = "Unmatched",
                        trader = order.trader.id,
                        odds = order.odds.value.toDouble()
                    )
                } else {
                    val bestLay = lays.values.first()[0]
                    exchangeQueries.insert(
                        side = "Back",
                        operation = if (bestLay.odds.value >= order.odds.value) "Matched" else "Unmatched",
                        trader = order.trader.id,
                        odds = order.odds.value.toDouble()
                    )
                }
            }
            is Order.Lay -> {
                if (backs.values.isEmpty()) {
                    exchangeQueries.insert(
                        side = "Lay",
                        operation = "Unmatched",
                        trader = order.trader.id,
                        odds = order.odds.value.toDouble()
                    )
                } else {
                    val bestBack = backs.values.first()[0]
                    exchangeQueries.insert(
                        side = "Lay",
                        operation = if (bestBack.odds.value <= order.odds.value) "Matched" else "Unmatched",
                        trader = order.trader.id,
                        odds = order.odds.value.toDouble()
                    )
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
