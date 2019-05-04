@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

import com.veyndan.thesis.InsufficientFundsException
import com.veyndan.thesis.exchange.*
import com.veyndan.thesis.toPounds
import io.kotlintest.shouldThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MarketTest {

    @Test
    fun populate() {
        val bettors = listOf(Bettor(Bettor.Id(0U), 10.toPounds(), 0U))

        val exchange = Market(bettors).apply {
            addOrder(Order.Back(Bettor.Id(0U), 2.toPounds(), 1.8.toOdds()))
            addOrder(Order.Lay(Bettor.Id(0U), 2.toPounds(), 1.7.toOdds()))
        }

        assertEquals(
            listOf(1.8.toOdds() to listOf(Order.Back(Bettor.Id(0U), 2.toPounds(), 1.8.toOdds()))),
            exchange.orderBook.backs.toList()
        )
        assertEquals(
            listOf(1.7.toOdds() to listOf(Order.Lay(Bettor.Id(0U), 2.toPounds(), 1.7.toOdds()))),
            exchange.orderBook.lays.toList()
        )
    }

    @Test
    fun populateMultipleBackOrdersAtSameOdds() {
        val bettors = listOf(
            Bettor(Bettor.Id(0U), funds = 10.toPounds(), dryRunCount = 0U),
            Bettor(Bettor.Id(1U), funds = 10.toPounds(), dryRunCount = 0U),
            Bettor(Bettor.Id(2U), funds = 10.toPounds(), dryRunCount = 0U)
        )
        val exchange = Market(bettors).apply {
            addOrder(Order.Back(Bettor.Id(0U), 2.toPounds(), 1.7.toOdds()))
            addOrder(Order.Back(Bettor.Id(1U), 2.toPounds(), 1.8.toOdds()))
            addOrder(Order.Back(Bettor.Id(2U), 2.toPounds(), 1.7.toOdds()))
        }

        assertEquals(
            listOf(
                1.7.toOdds() to listOf(
                    Order.Back(Bettor.Id(0U), 2.toPounds(), 1.7.toOdds()),
                    Order.Back(Bettor.Id(2U), 2.toPounds(), 1.7.toOdds())
                ),
                1.8.toOdds() to listOf(
                    Order.Back(Bettor.Id(1U), 2.toPounds(), 1.8.toOdds())
                )
            ),
            exchange.orderBook.backs.toList()
        )
    }

    @Test
    fun populateMultipleLayOrdersAtSameOdds() {
        val bettors = listOf(
            Bettor(Bettor.Id(0U), funds = 100.toPounds(), dryRunCount = 0U),
            Bettor(Bettor.Id(1U), funds = 100.toPounds(), dryRunCount = 0U),
            Bettor(Bettor.Id(2U), funds = 100.toPounds(), dryRunCount = 0U)
        )

        val exchange = Market(bettors).apply {
            addOrder(Order.Lay(Bettor.Id(0U), 10.toPounds(), 1.7.toOdds()))
            addOrder(Order.Lay(Bettor.Id(1U), 10.toPounds(), 1.8.toOdds()))
            addOrder(Order.Lay(Bettor.Id(2U), 10.toPounds(), 1.7.toOdds()))
        }

        assertEquals(
            listOf(
                1.8.toOdds() to listOf(
                    Order.Lay(Bettor.Id(1U), 10.toPounds(), 1.8.toOdds())
                ),
                1.7.toOdds() to listOf(
                    Order.Lay(Bettor.Id(0U), 10.toPounds(), 1.7.toOdds()),
                    Order.Lay(Bettor.Id(2U), 10.toPounds(), 1.7.toOdds())
                )
            ),
            exchange.orderBook.lays.toList()
        )
    }

    @Test
    fun orderedBacksWhenPopulating() {
        val bettors = listOf(Bettor(Bettor.Id(0U), funds = 100.toPounds(), dryRunCount = 0U))

        val exchange = Market(bettors).apply {
            addOrder(Order.Back(Bettor.Id(0U), 10.toPounds(), 2.2.toOdds()))
            addOrder(Order.Back(Bettor.Id(0U), 10.toPounds(), 1.8.toOdds()))
            addOrder(Order.Back(Bettor.Id(0U), 10.toPounds(), 1.7.toOdds()))
        }

        assertEquals(
            listOf(
                1.7.toOdds() to listOf(Order.Back(Bettor.Id(0U), 10.toPounds(), 1.7.toOdds())),
                1.8.toOdds() to listOf(Order.Back(Bettor.Id(0U), 10.toPounds(), 1.8.toOdds())),
                2.2.toOdds() to listOf(Order.Back(Bettor.Id(0U), 10.toPounds(), 2.2.toOdds()))
            ),
            exchange.orderBook.backs.toList()
        )
    }

    @Test
    fun orderedLaysWhenPopulating() {
        val bettors = listOf(Bettor(Bettor.Id(0U), funds = 100.toPounds(), dryRunCount = 0U))

        val exchange = Market(bettors).apply {
            addOrder(Order.Lay(Bettor.Id(0U), 10.toPounds(), 1.8.toOdds()))
            addOrder(Order.Lay(Bettor.Id(0U), 10.toPounds(), 1.7.toOdds()))
            addOrder(Order.Lay(Bettor.Id(0U), 10.toPounds(), 2.2.toOdds()))
        }

        assertEquals(
            listOf(
                2.2.toOdds() to listOf(Order.Lay(Bettor.Id(0U), 10.toPounds(), 2.2.toOdds())),
                1.8.toOdds() to listOf(Order.Lay(Bettor.Id(0U), 10.toPounds(), 1.8.toOdds())),
                1.7.toOdds() to listOf(Order.Lay(Bettor.Id(0U), 10.toPounds(), 1.7.toOdds()))
            ),
            exchange.orderBook.lays.toList()
        )
    }

    @Test
    fun `A bettor cancels a back order`() {
        val bettors = listOf(Bettor(Bettor.Id(0U), funds = 100.toPounds(), dryRunCount = 0U))

        val exchange = Market(bettors).apply {
            addOrder(Order.Back(Bettor.Id(0U), 10.toPounds(), 1.8.toOdds()))
            addOrder(Order.Back(Bettor.Id(0U), 10.toPounds(), 1.7.toOdds()))
        }

        exchange.cancelOrder(Order.Back(Bettor.Id(0U), 10.toPounds(), 1.8.toOdds()))

        assertEquals(
            listOf(1.7.toOdds() to listOf(Order.Back(Bettor.Id(0U), 10.toPounds(), 1.7.toOdds()))),
            exchange.orderBook.backs.toList()
        )

        assertEquals(90.toPounds(), exchange.bettors[0].funds)
    }

    @Test
    fun `A bettor cancels a lay order`() {
        val bettors = listOf(Bettor(Bettor.Id(0U), funds = 100.toPounds(), dryRunCount = 0U))

        val exchange = Market(bettors).apply {
            addOrder(Order.Lay(Bettor.Id(0U), 10.toPounds(), 1.8.toOdds()))
            addOrder(Order.Lay(Bettor.Id(0U), 10.toPounds(), 1.7.toOdds()))
        }

        exchange.cancelOrder(Order.Lay(Bettor.Id(0U), 10.toPounds(), 1.8.toOdds()))

        assertEquals(
            listOf(1.7.toOdds() to listOf(Order.Lay(Bettor.Id(0U), 10.toPounds(), 1.7.toOdds()))),
            exchange.orderBook.lays.toList()
        )

        assertEquals(93.toPounds(), exchange.bettors[0].funds)
    }

    @Test
    fun marketOrderSamePricedBack() {
        val bettors = listOf(Bettor(Bettor.Id(0U), funds = 100.toPounds(), dryRunCount = 0U))

        val exchange = Market(bettors).apply {
            addOrder(Order.Lay(Bettor.Id(0U), 10.toPounds(), 1.7.toOdds()))
            addOrder(Order.Lay(Bettor.Id(0U), 10.toPounds(), 1.9.toOdds()))
            addOrder(Order.Back(Bettor.Id(0U), 10.toPounds(), 1.8.toOdds()))
        }

        assertEquals(
            emptyList<Pair<Odds, MutableList<Order.Lay>>>(),
            exchange.orderBook.backs.toList()
        )

        assertEquals(
            listOf(1.7.toOdds() to listOf(Order.Lay(Bettor.Id(0U), 10.toPounds(), 1.7.toOdds()))),
            exchange.orderBook.lays.toList()
        )
    }

    @Test
    fun marketOrderSamePricedLay() {
        val bettors = listOf(Bettor(Bettor.Id(0U), funds = 100.toPounds(), dryRunCount = 0U))

        val exchange = Market(bettors).apply {
            addOrder(Order.Back(Bettor.Id(0U), 10.toPounds(), 1.7.toOdds()))
            addOrder(Order.Back(Bettor.Id(0U), 10.toPounds(), 1.9.toOdds()))
            addOrder(Order.Lay(Bettor.Id(0U), 10.toPounds(), 1.8.toOdds()))
        }

        assertEquals(
            listOf(1.9.toOdds() to listOf(Order.Back(Bettor.Id(0U), 10.toPounds(), 1.9.toOdds()))),
            exchange.orderBook.backs.toList()
        )

        assertEquals(emptyList<Pair<Odds, MutableList<Order.Lay>>>(), exchange.orderBook.lays.toList())
    }

    @Test
    fun marketOrderMatchBack() {
        val bettors = listOf(Bettor(Bettor.Id(0U), funds = 100.toPounds(), dryRunCount = 0U))

        val exchange = Market(bettors).apply {
            addOrder(Order.Lay(Bettor.Id(0U), 10.toPounds(), 1.7.toOdds()))
            addOrder(Order.Lay(Bettor.Id(0U), 10.toPounds(), 1.9.toOdds()))
            addOrder(Order.Back(Bettor.Id(0U), 6.toPounds(), 1.8.toOdds()))
        }

        assertEquals(
            emptyList<Pair<Odds, MutableList<Order.Lay>>>(),
            exchange.orderBook.backs.toList()
        )

        assertEquals(
            listOf(
                1.9.toOdds() to listOf(Order.Lay(Bettor.Id(0U), 4.toPounds(), 1.9.toOdds())),
                1.7.toOdds() to listOf(Order.Lay(Bettor.Id(0U), 10.toPounds(), 1.7.toOdds()))
            ),
            exchange.orderBook.lays.toList()
        )
    }

    @Test
    fun recursiveMarketOrderMatchBack() {
        val bettors = listOf(Bettor(Bettor.Id(0U), funds = 100.toPounds(), dryRunCount = 0U))

        val exchange = Market(bettors).apply {
            addOrder(Order.Lay(Bettor.Id(0U), 3.toPounds(), 1.7.toOdds()))
            addOrder(Order.Lay(Bettor.Id(0U), 5.toPounds(), 1.9.toOdds()))
            addOrder(Order.Lay(Bettor.Id(0U), 32.toPounds(), 1.6.toOdds()))
            addOrder(Order.Back(Bettor.Id(0U), 28.toPounds(), 1.5.toOdds()))
        }

        assertEquals(
            emptyList<Pair<Odds, MutableList<Order.Lay>>>(),
            exchange.orderBook.backs.toList()
        )

        assertEquals(
            listOf(1.6.toOdds() to listOf(Order.Lay(Bettor.Id(0U), 12.toPounds(), 1.6.toOdds()))),
            exchange.orderBook.lays.toList()
        )
    }

    @Test
    fun recursiveMarketOrderPartialMatchBack() {
        val bettors = listOf(Bettor(Bettor.Id(0U), funds = 100.toPounds(), dryRunCount = 0U))

        val exchange = Market(bettors).apply {
            addOrder(Order.Lay(Bettor.Id(0U), 3.toPounds(), 1.7.toOdds()))
            addOrder(Order.Lay(Bettor.Id(0U), 5.toPounds(), 1.9.toOdds()))
            addOrder(Order.Lay(Bettor.Id(0U), 11.toPounds(), 1.6.toOdds()))
            addOrder(Order.Back(Bettor.Id(0U), 28.toPounds(), 1.5.toOdds()))
        }

        assertEquals(
            listOf(1.5.toOdds() to listOf(Order.Back(Bettor.Id(0U), 9.toPounds(), 1.5.toOdds()))),
            exchange.orderBook.backs.toList()
        )

        assertEquals(
            emptyList<Pair<Odds, MutableList<Order.Lay>>>(),
            exchange.orderBook.lays.toList()
        )
    }

    @Test
    fun marketOrderMatchLay() {
        val bettors = listOf(Bettor(Bettor.Id(0U), funds = 100.toPounds(), dryRunCount = 0U))

        val exchange = Market(bettors).apply {
            addOrder(Order.Back(Bettor.Id(0U), 10.toPounds(), 1.6.toOdds()))
            addOrder(Order.Back(Bettor.Id(0U), 10.toPounds(), 1.9.toOdds()))
            addOrder(Order.Lay(Bettor.Id(0U), 6.toPounds(), 1.8.toOdds()))
        }

        assertEquals(
            listOf(
                1.6.toOdds() to listOf(Order.Back(Bettor.Id(0U), 4.toPounds(), 1.6.toOdds())),
                1.9.toOdds() to listOf(Order.Back(Bettor.Id(0U), 10.toPounds(), 1.9.toOdds()))
            ),
            exchange.orderBook.backs.toList()
        )

        assertEquals(
            emptyList<Pair<Odds, MutableList<Order.Lay>>>(),
            exchange.orderBook.lays.toList()
        )
    }

    @Test
    fun recursiveMarketOrderMatchLay() {
        val bettors = listOf(Bettor(Bettor.Id(0U), funds = 100.toPounds(), dryRunCount = 0U))

        val exchange = Market(bettors).apply {
            addOrder(Order.Back(Bettor.Id(0U), 3.toPounds(), 1.7.toOdds()))
            addOrder(Order.Back(Bettor.Id(0U), 32.toPounds(), 1.9.toOdds()))
            addOrder(Order.Back(Bettor.Id(0U), 5.toPounds(), 1.6.toOdds()))
            addOrder(Order.Lay(Bettor.Id(0U), 28.toPounds(), 2.toOdds()))
        }

        assertEquals(
            listOf(1.9.toOdds() to listOf(Order.Back(Bettor.Id(0U), 12.toPounds(), 1.9.toOdds()))),
            exchange.orderBook.backs.toList()
        )

        assertEquals(
            emptyList<Pair<Odds, MutableList<Order.Lay>>>(),
            exchange.orderBook.lays.toList()
        )
    }

    @Test
    fun recursiveMarketOrderPartialMatchLay() {
        val bettors = listOf(Bettor(Bettor.Id(0U), funds = 100.toPounds(), dryRunCount = 0U))

        val exchange = Market(bettors).apply {
            addOrder(Order.Back(Bettor.Id(0U), 3.toPounds(), 1.7.toOdds()))
            addOrder(Order.Back(Bettor.Id(0U), 11.toPounds(), 1.9.toOdds()))
            addOrder(Order.Back(Bettor.Id(0U), 5.toPounds(), 1.6.toOdds()))
            addOrder(Order.Lay(Bettor.Id(0U), 28.toPounds(), 2.toOdds()))
        }

        assertEquals(
            emptyList<Pair<Odds, MutableList<Order.Back>>>(),
            exchange.orderBook.backs.toList()
        )

        assertEquals(
            listOf(2.toOdds() to listOf(Order.Lay(Bettor.Id(0U), 9.toPounds(), 2.toOdds()))),
            exchange.orderBook.lays.toList()
        )
    }

    @Test
    fun `Funds reduced after limit order`() {
        val exchange = Market(listOf(Bettor(Bettor.Id(0U), funds = 100.toPounds(), dryRunCount = 0U)))

        exchange.addOrder(Order.Back(Bettor.Id(0U), 5.toPounds(), 1.8.toOdds()))
        assertEquals(95.toPounds(), exchange.bettors[0].funds)

        exchange.addOrder(Order.Lay(Bettor.Id(0U), 5.toPounds(), 1.7.toOdds()))
        assertEquals(91.5.toPounds(), exchange.bettors[0].funds)
    }

    @Test
    fun `Funds reduced after exact market order`() {
        val bettors = listOf(
            Bettor(Bettor.Id(0U), funds = 10.toPounds(), dryRunCount = 0U),
            Bettor(Bettor.Id(1U), funds = 10.toPounds(), dryRunCount = 0U)
        )

        val exchange = Market(bettors).apply {
            addOrder(Order.Back(Bettor.Id(0U), 5.toPounds(), 1.7.toOdds()))
            addOrder(Order.Lay(Bettor.Id(1U), 5.toPounds(), 1.8.toOdds()))
        }

        assertEquals(5.toPounds(), exchange.bettors[0].funds)
        assertEquals(6.toPounds(), exchange.bettors[1].funds)
    }

    @Test
    fun `Funds reduced after partial market order`() {
        val bettors = listOf(
            Bettor(Bettor.Id(0U), funds = 10.toPounds(), dryRunCount = 0U),
            Bettor(Bettor.Id(1U), funds = 10.toPounds(), dryRunCount = 0U)
        )

        val exchange = Market(bettors).apply {
            addOrder(Order.Back(Bettor.Id(0U), 3.toPounds(), 1.7.toOdds()))
            addOrder(Order.Lay(Bettor.Id(1U), 5.toPounds(), 1.8.toOdds()))
        }

        assertEquals(7.toPounds(), exchange.bettors[0].funds)
        assertEquals(6.toPounds(), exchange.bettors[1].funds)
    }

    @Test
    fun `Funds reduced after recursive market order`() {
        val bettors = listOf(
            Bettor(Bettor.Id(0U), funds = 100.toPounds(), dryRunCount = 0U),
            Bettor(Bettor.Id(1U), funds = 100.toPounds(), dryRunCount = 0U)
        )

        val exchange = Market(bettors).apply {
            addOrder(Order.Back(Bettor.Id(0U), 7.toPounds(), 1.5.toOdds()))
            addOrder(Order.Lay(Bettor.Id(1U), 3.toPounds(), 1.3.toOdds()))
            addOrder(Order.Back(Bettor.Id(0U), 6.toPounds(), 1.7.toOdds()))
            addOrder(Order.Lay(Bettor.Id(1U), 5.toPounds(), 1.8.toOdds()))
        }

        assertEquals(87.toPounds(), exchange.bettors[0].funds)
        assertEquals(95.1.toPounds(), exchange.bettors[1].funds)
    }

    @Test
    fun `A bettor doesn't have enough funds to place order`() {
        // Throw NoeEnoughFundsException if out of funds
        val exchange = Market(listOf(Bettor(Bettor.Id(0U), funds = 10.toPounds(), dryRunCount = 0U)))

        shouldThrow<InsufficientFundsException> {
            exchange.addOrder(Order.Back(Bettor.Id(0U), 20.toPounds(), 1.7.toOdds()))
        }

        assertEquals(10.toPounds(), exchange.bettors[0].funds)
    }

    @Test
    fun `Round money when greater than 2 decimal places`() {
        val order = Order.Lay(Bettor.Id(0U), 12.34.toPounds(), 1.23.toOdds())
        assertEquals(2.84.toPounds(), order.liability)

        val exchange = Market(listOf(Bettor(Bettor.Id(0U), funds = 10.toPounds(), dryRunCount = 0U))).apply {
            addOrder(order)
        }

        assertEquals(7.16.toPounds(), exchange.bettors[0].funds)
    }
}
