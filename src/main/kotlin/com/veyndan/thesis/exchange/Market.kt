@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis.exchange

class Market(val bettors: List<Bettor>) {

    val orderBook = OrderBook()

    init {
        assert(bettors == bettors.distinctBy { it.id }) { "Each bettor must have a unique ID" }
    }

    fun addOrder(order: Order) {
        bettors.first { it.id == order.bettorId }.funds -= order.liability
        orderBook += order
    }

    fun cancelOrder(order: Order) {
        bettors.first { it.id == order.bettorId }.funds += order.liability
        orderBook -= order
    }
}
