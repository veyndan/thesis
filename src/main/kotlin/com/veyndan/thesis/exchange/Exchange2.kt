//package exchange
//
//import java.io.File
//
///**
// * Minimum price in the system, in cents/pennies.
// */
//const val BSE_SYS_MINPRICE = 1L
//
///**
// * Maximum price in the system, in cents/pennies.
// */
//const val BSE_SYS_MAXPRICE = 1000L
//
///**
// * Minimum change in price, in cents/pennies.
// */
//const val TICKSIZE = 1
//
///**
// * @property tid Trader ID
// * @property qty Quantity
// * @property time timestamp
// * @property qid Quote ID (unique to each quote)
// */
//data class Order(val tid: String, val type: Any, val price: Long, val qty: Long, val time: Double?, var qid: Long?) {
//
//    override fun toString(): String {
//        return "[$tid $type P=${"%03d".format(price)} Q=$qty T=${"%5.2f".format(time)} QID:$qid]"
//    }
//}
//
//data class OrderInfos(val qty: Long, val orderInfos: List<OrderInfo>)
//
//data class OrderInfo(val time: Any?, val qty: Any?, val tid: String, val qid: Any?)
//
///**
// * One side of the book: a list of bids or a list of asks, each sorted best-first.
// *
// * @property booktype Bids or asks?
// */
//class OrderbookHalf(val booktype: String, val worstprice: Long) {
//
//    /**
//     * Dictionary of orders received, indexed by Trader ID
//     */
//    val orders = hashMapOf<String, Order>()
//
//    /**
//     * limit order book, dictionary indexed by price, with order info
//     */
//    var lob = hashMapOf<Long, OrderInfos>()
//
//    /**
//     * anonymized LOB, lists, with only price/qty info
//     */
//    var lobAnon = mutableListOf<Pair<Long, Any>>()
//
//    /**
//     * summary stats
//     */
//    var bestPrice: Long? = null
//
//    var bestTid: String? = null
//
//    /**
//     * How many orders?
//     */
//    var nOrders = 0
//
//    /**
//     * How many different prices on lob?
//     */
//    var lobDepth = 0
//
//    /**
//     * Anonymize a lob, strip out order details, format as a sorted list. NB for asks, the sorting should be reversed.
//     */
//    fun anonymizeLob() {
//        lobAnon = mutableListOf()
//        for (price in lob.keys.sorted()) {
//            val qty = lob.getValue(price).qty
//            lobAnon.add(price to qty)
//        }
//    }
//
//    /**
//     * take a list of orders and build a limit-order-book (lob) from it
//     * NB the exchange needs to know arrival times and trader-id associated with each order
//     * returns lob as a dictionary (i.e., unsorted)
//     * also builds anonymized version (just price/quantity, sorted, as a list) for publishing to traders
//     */
//    fun buildLob() {
//        lob = hashMapOf()
//
//        orders.forEach { _, order ->
//            val price = order.price
//
//            if (price in lob) {
//                // Update existing entry
//                val qty = lob.getValue(price).qty
//                val orderList = lob.getValue(price).orderInfos + OrderInfo(order.time, order.qty, order.tid, order.qid)
//                lob[price] = OrderInfos(qty + order.qty, orderList)
//            } else {
//                // Create a new dictionary entry
//                lob[price] = OrderInfos(order.qty, listOf(OrderInfo(order.time, order.qty, order.tid, order.qid)))
//            }
//        }
//
//        // Create anonymized version
//        anonymizeLob()
//
//        // Record best price and associated trader-id
//        if (lob.size > 0) {
//            bestPrice = if (booktype == "Bid") {
//                lobAnon.last().first
//            } else {
//                lobAnon[0].first
//            }
//
//            bestTid = lob.getValue(bestPrice!!).orderInfos[0].tid
//        } else {
//            bestPrice = null
//            bestTid = null
//        }
//    }
//
//    /**
//     * add order to the dictionary holding the list of orders
//     * either overwrites old order from this trader
//     * or dynamically creates new entry in the dictionary
//     * so, max of one order per trader per list
//     * checks whether length or order list has changed, to distinguish addition/overwrite
//     * print('book_add > %s %s' % (order, self.orders))
//     */
//    fun bookAdd(order: Order): String {
//        val nOrdersTmp = nOrders
//        orders[order.tid] = order
//        nOrders = orders.size
//        buildLob()
//
//        return if (nOrdersTmp != nOrders) "Addition" else "Overwrite"
//    }
//
//    /**
//     * delete order from the dictionary holding the orders
//     * assumes max of one order per trader per list
//     * checks that the Trader ID does actually exist in the dict before deletion
//     * print('book_del %s',self.orders)
//     */
//    fun bookDel(order: Order) {
//        if (orders[order.tid] != null) {
//            orders.com.veyndan.thesis.remove(order.tid)
//            nOrders = orders.size
//            buildLob()
//        }
//    }
//
//    /**
//     * delete order: when the best bid/ask has been hit, delete it from the book
//     * the TraderID of the deleted order is return-value, as counterparty to the trade
//     */
//    fun deleteBest(): String {
//        val bestPriceOrders = lob.getValue(bestPrice!!)
//        val bestPriceQty = bestPriceOrders.qty
//        val bestPriceCounterparty = bestPriceOrders.orderInfos[0].tid
//
//        if (bestPriceQty == 1L) {
//            // Here the order deletes the best price
//            lob.com.veyndan.thesis.remove(bestPrice!!)
//            orders.com.veyndan.thesis.remove(bestPriceCounterparty)
//            nOrders -= 1
//            if (nOrders > 0) {
//                bestPrice = if (booktype == "Bid") lob.keys.max() else lob.keys.min()
//                lobDepth = lob.keys.size
//            } else {
//                bestPrice = worstprice
//                lobDepth = 0
//            }
//        } else {
//            // Best_bid_qty>1 so the order decrements the quantity of the best bid
//            // Update the lob with the decremented order data
//            lob[bestPrice!!] = OrderInfos(bestPriceQty - 1, bestPriceOrders.orderInfos.drop(1))
//
//            // Update the bid list: counterparty's bid has been deleted
//            orders.com.veyndan.thesis.remove(bestPriceCounterparty)
//            nOrders -= 1
//        }
//
//        buildLob()
//        return bestPriceCounterparty
//    }
//}
//
//sealed class Record(val type: String) {
//
//    data class Cancel(val time: Any, val order: Order) : Record("Cancel")
//
//    data class Trade(val time: Double, val price: Long, val party1: String, val party2: String, val qty: Long) :
//        Record("Trade")
//}
//
///**
// * Orderbook for a single instrument: list of bids and list of asks
// */
//open class Orderbook {
//
//    val bids = OrderbookHalf("Bid", BSE_SYS_MINPRICE)
//    val asks = OrderbookHalf("Ask", BSE_SYS_MAXPRICE)
//    val tape = mutableListOf<Record>()
//
//    /**
//     * Unique ID code for each quote accepted onto the book
//     */
//    var quoteId = 0L
//}
//
///**
// * Exchange's internal orderbook.
// */
//class Exchange : Orderbook() {
//
//    fun addOrder(order: Order): Pair<Long?, String> {
//        // Add a quote/order to the exchange and update all internal records; return unique i.d.
//        order.qid = quoteId
//        quoteId = order.qid!! + 1
//
//        val response: String
//
//        if (order.type == "Bid") {
//            response = bids.bookAdd(order)
//            val bestPrice = bids.lobAnon.last().first
//            bids.bestPrice = bestPrice
//            bids.bestTid = bids.lob.getValue(bestPrice).orderInfos[0].tid
//        } else {
//            response = asks.bookAdd(order)
//            val bestPrice = asks.lobAnon[0].first
//            asks.bestPrice = bestPrice
//            asks.bestTid = asks.lob.getValue(bestPrice).orderInfos[0].tid
//        }
//
//        return order.qid to response
//    }
//
//    fun delOrder(time: Any, order: Order) {
//        when (order.type) {
//            "Bid" -> {
//                bids.bookDel(order)
//                if (bids.nOrders > 0) {
//                    val bestPrice = bids.lobAnon.last().first
//                    bids.bestPrice = bestPrice
//                    bids.bestTid = bids.lob.getValue(bestPrice).orderInfos[0].tid
//                } else {
//                    // This side of the book is empty
//                    bids.bestPrice = null
//                    bids.bestTid = null
//                }
//
//                tape.add(Record.Cancel(time, order))
//            }
//            "Ask" -> {
//                asks.bookDel(order)
//                if (asks.nOrders > 0) {
//                    val bestPrice = asks.lobAnon[0].first
//                    asks.bestPrice = bestPrice
//                    asks.bestTid = asks.lob.getValue(bestPrice).orderInfos[0].tid
//                } else {
//                    // This side of the book is empty
//                    asks.bestPrice = null
//                    asks.bestTid = null
//                }
//
//                tape.add(Record.Cancel(time, order))
//            }
//            else -> error("Bad order type")
//        }
//    }
//
//    /**
//     * receive an order and either add it to the relevant LOB (ie treat as limit order)
//     * or if it crosses the best counterparty offer, execute it (treat as a market order)
//     */
//    fun processOrder2(time: Double, order: Order, verbose: Boolean): Record.Trade? {
//        val oprice = order.price
//        var counterparty: String? = null
//
//        // Add it to the order lists -- overwriting any previous order
//        val (qid, response) = addOrder(order)
//        order.qid = qid
//
//        if (verbose) {
//            println("QUID: order.quid=${order.qid}")
//            println("RESPONSE: $response")
//        }
//
//        val bestAsk = asks.bestPrice
//        val bestAskTid = asks.bestTid
//        val bestBid = bids.bestPrice
//        val bestBidTid = bids.bestTid
//
//        var price: Long? = null
//
//        when (order.type) {
//            "Bid" -> {
//                if (asks.nOrders > 0 && bestBid!! >= bestAsk!!) {
//                    // Bid lifts the best ask
//                    if (verbose) println("Bid \$$oprice lifts best ask")
//                    counterparty = bestAskTid
//
//                    // Bid crossed ask, so use ask price
//                    price = bestAsk
//                    if (verbose) println("counterparty, price $counterparty $price")
//
//                    // Delete the asks just crossed
//                    asks.deleteBest()
//
//                    // Delete the bid that was the latest order
//                    bids.deleteBest()
//                }
//            }
//            "Ask" -> {
//                if (bids.nOrders > 0 && bestAsk!! <= bestBid!!) {
//                    // Ask hits the best bid
//                    if (verbose) println("Ask \$$oprice hits best bid")
//
//                    // Remove the best bid
//                    counterparty = bestBidTid
//
//                    // Ask crossed bid, so use bid price
//                    price = bestBid
//
//                    if (verbose) println("counterparty, price $counterparty $price")
//
//                    // Delete the bid just crossed, from the exchange's records
//                    bids.deleteBest()
//
//                    // Delete the ask that was the latest order, from the exchange's records
//                    asks.deleteBest()
//                }
//            }
//            else -> error("Bad order type")
//        }
//
//        // NB at this point we have deleted the order from the exchange's records
//        // but the two traders concerned still have to be notified
//
//        if (verbose) println("counterparty $counterparty")
//
//        return if (counterparty != null) {
//            // Process the trade
//            if (verbose) println(">>>>>>>>>>>>>>>>>TRADE t=${"%5.2f".format(time)} \$${price!!} $counterparty ${order.tid}")
//
//            val transactionRecord = Record.Trade(time, price!!, counterparty, order.tid, order.qty)
//            tape.add(transactionRecord)
//            transactionRecord
//        } else {
//            null
//        }
//    }
//
//    fun tapeDump(fileName: String) {
//        File(fileName).writeText(tape.filterIsInstance<Record.Trade>().joinToString { "${it.time}, ${it.price}\n" })
//    }
//
//    /**
//     * this returns the LOB data "published" by the exchange,
//     * i.e., what is accessible to the traders
//     */
//    fun publishLob(time: Double, verbose: Boolean): Map<String, Any> {
//        val publicData = mapOf(
//            "time" to time,
//            "bids" to mapOf(
//                "best" to bids.bestPrice,
//                "worst" to bids.worstprice,
//                "n" to bids.nOrders,
//                "lob" to bids.lobAnon
//            ),
//            "asks" to mapOf(
//                "best" to asks.bestPrice,
//                "worst" to asks.worstprice,
//                "n" to asks.nOrders,
//                "lob" to asks.lobAnon
//            ),
//            "QID" to quoteId,
//            "tape" to tape
//        )
//
//        if (verbose) {
//            println("publish_lob: t=$time")
//            println("BID_lob=${(publicData.getValue("bids") as Map<String, Any>).getValue("lob")}")
//            println("ASK_lob=${(publicData.getValue("asks") as Map<String, Any>).getValue("lob")}")
//        }
//
//        return publicData
//    }
//}
