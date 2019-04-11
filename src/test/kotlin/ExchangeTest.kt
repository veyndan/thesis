@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

import com.veyndan.thesis.exchange.*
import com.veyndan.thesis.toPounds
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExchangeTest {

    @Test
    fun populate() {
        val bettors = listOf(Bettor(Bettor.Id(0U), 10.toPounds()))

        val exchange = Exchange(bettors).apply {
            addOrder(Order.Back(Bettor.Id(0U), 1.toPounds(), 1.8.toOdds()))
            addOrder(Order.Lay(Bettor.Id(0U), 1.toPounds(), 1.7.toOdds()))
        }

        assertEquals(
            listOf(1.8.toOdds() to listOf(Order.Back(Bettor.Id(0U), 1.toPounds(), 1.8.toOdds()))),
            exchange.backs.toList()
        )
        assertEquals(
            listOf(1.7.toOdds() to listOf(Order.Lay(Bettor.Id(0U), 1.toPounds(), 1.7.toOdds()))),
            exchange.lays.toList()
        )
    }

    @Test
    fun populateMultipleBackOrdersAtSameOdds() {
        val bettors = listOf(
            Bettor(Bettor.Id(0U), funds = 10.toPounds()),
            Bettor(Bettor.Id(1U), funds = 10.toPounds()),
            Bettor(Bettor.Id(2U), funds = 10.toPounds())
        )
        val exchange = Exchange(bettors).apply {
            addOrder(Order.Back(Bettor.Id(0U), 1.toPounds(), 1.7.toOdds()))
            addOrder(Order.Back(Bettor.Id(1U), 1.toPounds(), 1.8.toOdds()))
            addOrder(Order.Back(Bettor.Id(2U), 1.toPounds(), 1.7.toOdds()))
        }

        assertEquals(
            listOf(
                1.7.toOdds() to listOf(
                    Order.Back(Bettor.Id(0U), 1.toPounds(), 1.7.toOdds()),
                    Order.Back(Bettor.Id(2U), 1.toPounds(), 1.7.toOdds())
                ),
                1.8.toOdds() to listOf(
                    Order.Back(Bettor.Id(1U), 1.toPounds(), 1.8.toOdds())
                )
            ),
            exchange.backs.toList()
        )
    }

    @Test
    fun populateMultipleLayOrdersAtSameOdds() {
        val bettors = listOf(
            Bettor(Bettor.Id(0U), funds = 10.toPounds()),
            Bettor(Bettor.Id(1U), funds = 10.toPounds()),
            Bettor(Bettor.Id(2U), funds = 10.toPounds())
        )

        val exchange = Exchange(bettors).apply {
            addOrder(Order.Lay(Bettor.Id(0U), 1.toPounds(), 1.7.toOdds()))
            addOrder(Order.Lay(Bettor.Id(1U), 1.toPounds(), 1.8.toOdds()))
            addOrder(Order.Lay(Bettor.Id(2U), 1.toPounds(), 1.7.toOdds()))
        }

        assertEquals(
            listOf(
                1.8.toOdds() to listOf(
                    Order.Lay(Bettor.Id(1U), 1.toPounds(), 1.8.toOdds())
                ),
                1.7.toOdds() to listOf(
                    Order.Lay(Bettor.Id(0U), 1.toPounds(), 1.7.toOdds()),
                    Order.Lay(Bettor.Id(2U), 1.toPounds(), 1.7.toOdds())
                )
            ),
            exchange.lays.toList()
        )
    }

    @Test
    fun orderedBacksWhenPopulating() {
        val bettors = listOf(Bettor(Bettor.Id(0U), funds = 10.toPounds()))

        val exchange = Exchange(bettors).apply {
            addOrder(Order.Back(Bettor.Id(0U), 1.toPounds(), 2.2.toOdds()))
            addOrder(Order.Back(Bettor.Id(0U), 1.toPounds(), 1.8.toOdds()))
            addOrder(Order.Back(Bettor.Id(0U), 1.toPounds(), 1.7.toOdds()))
        }

        assertEquals(
            listOf(
                1.7.toOdds() to listOf(Order.Back(Bettor.Id(0U), 1.toPounds(), 1.7.toOdds())),
                1.8.toOdds() to listOf(Order.Back(Bettor.Id(0U), 1.toPounds(), 1.8.toOdds())),
                2.2.toOdds() to listOf(Order.Back(Bettor.Id(0U), 1.toPounds(), 2.2.toOdds()))
            ),
            exchange.backs.toList()
        )
    }

    @Test
    fun orderedLaysWhenPopulating() {
        val bettors = listOf(Bettor(Bettor.Id(0U), funds = 10.toPounds()))

        val exchange = Exchange(bettors).apply {
            addOrder(Order.Lay(Bettor.Id(0U), 1.toPounds(), 1.8.toOdds()))
            addOrder(Order.Lay(Bettor.Id(0U), 1.toPounds(), 1.7.toOdds()))
            addOrder(Order.Lay(Bettor.Id(0U), 1.toPounds(), 2.2.toOdds()))
        }

        assertEquals(
            listOf(
                2.2.toOdds() to listOf(Order.Lay(Bettor.Id(0U), 1.toPounds(), 2.2.toOdds())),
                1.8.toOdds() to listOf(Order.Lay(Bettor.Id(0U), 1.toPounds(), 1.8.toOdds())),
                1.7.toOdds() to listOf(Order.Lay(Bettor.Id(0U), 1.toPounds(), 1.7.toOdds()))
            ),
            exchange.lays.toList()
        )
    }

    @Test
    fun `A bettor cancels a back order`() {
        val bettors = listOf(Bettor(Bettor.Id(0U), funds = 10.toPounds()))

        val exchange = Exchange(bettors).apply {
            addOrder(Order.Back(Bettor.Id(0U), 1.toPounds(), 1.8.toOdds()))
            addOrder(Order.Back(Bettor.Id(0U), 1.toPounds(), 1.7.toOdds()))
        }

        exchange.cancelOrder(Order.Back(Bettor.Id(0U), 1.toPounds(), 1.8.toOdds()))

        assertEquals(
            listOf(1.7.toOdds() to listOf(Order.Back(Bettor.Id(0U), 1.toPounds(), 1.7.toOdds()))),
            exchange.backs.toList()
        )

        assertEquals(9.toPounds(), exchange.bettors[0].funds)
    }

    @Test
    fun `A bettor cancels a lay order`() {
        val bettors = listOf(Bettor(Bettor.Id(0U), funds = 10.toPounds()))

        val exchange = Exchange(bettors).apply {
            addOrder(Order.Lay(Bettor.Id(0U), 1.toPounds(), 1.8.toOdds()))
            addOrder(Order.Lay(Bettor.Id(0U), 1.toPounds(), 1.7.toOdds()))
        }

        exchange.cancelOrder(Order.Lay(Bettor.Id(0U), 1.toPounds(), 1.8.toOdds()))

        assertEquals(
            listOf(1.7.toOdds() to listOf(Order.Lay(Bettor.Id(0U), 1.toPounds(), 1.7.toOdds()))),
            exchange.lays.toList()
        )

        assertEquals(9.3.toPounds(), exchange.bettors[0].funds)
    }

    @Test
    fun marketOrderSamePricedBack() {
        val bettors = listOf(Bettor(Bettor.Id(0U), funds = 10.toPounds()))

        val exchange = Exchange(bettors).apply {
            addOrder(Order.Lay(Bettor.Id(0U), 1.toPounds(), 1.7.toOdds()))
            addOrder(Order.Lay(Bettor.Id(0U), 1.toPounds(), 1.9.toOdds()))
            addOrder(Order.Back(Bettor.Id(0U), 1.toPounds(), 1.8.toOdds()))
        }

        assertEquals(
            emptyList<Pair<Odds, MutableList<Order.Lay>>>(),
            exchange.backs.toList()
        )

        assertEquals(
            listOf(1.7.toOdds() to listOf(Order.Lay(Bettor.Id(0U), 1.toPounds(), 1.7.toOdds()))),
            exchange.lays.toList()
        )
    }

    @Test
    fun marketOrderSamePricedLay() {
        val bettors = listOf(Bettor(Bettor.Id(0U), funds = 10.toPounds()))

        val exchange = Exchange(bettors).apply {
            addOrder(Order.Back(Bettor.Id(0U), 1.toPounds(), 1.7.toOdds()))
            addOrder(Order.Back(Bettor.Id(0U), 1.toPounds(), 1.9.toOdds()))
            addOrder(Order.Lay(Bettor.Id(0U), 1.toPounds(), 1.8.toOdds()))
        }

        assertEquals(
            listOf(1.9.toOdds() to listOf(Order.Back(Bettor.Id(0U), 1.toPounds(), 1.9.toOdds()))),
            exchange.backs.toList()
        )

        assertEquals(emptyList<Pair<Odds, MutableList<Order.Lay>>>(), exchange.lays.toList())
    }

    @Test
    fun marketOrderMatchBack() {
        val bettors = listOf(Bettor(Bettor.Id(0U), funds = 10.toPounds()))

        val exchange = Exchange(bettors).apply {
            addOrder(Order.Lay(Bettor.Id(0U), 1.toPounds(), 1.7.toOdds()))
            addOrder(Order.Lay(Bettor.Id(0U), 1.toPounds(), 1.9.toOdds()))
            addOrder(Order.Back(Bettor.Id(0U), 0.6.toPounds(), 1.8.toOdds()))
        }

        assertEquals(
            emptyList<Pair<Odds, MutableList<Order.Lay>>>(),
            exchange.backs.toList()
        )

        assertEquals(
            listOf(
                1.9.toOdds() to listOf(Order.Lay(Bettor.Id(0U), 0.4.toPounds(), 1.9.toOdds())),
                1.7.toOdds() to listOf(Order.Lay(Bettor.Id(0U), 1.toPounds(), 1.7.toOdds()))
            ),
            exchange.lays.toList()
        )
    }

    @Test
    fun recursiveMarketOrderMatchBack() {
        val bettors = listOf(Bettor(Bettor.Id(0U), funds = 10.toPounds()))

        val exchange = Exchange(bettors).apply {
            addOrder(Order.Lay(Bettor.Id(0U), 0.3.toPounds(), 1.7.toOdds()))
            addOrder(Order.Lay(Bettor.Id(0U), 0.5.toPounds(), 1.9.toOdds()))
            addOrder(Order.Lay(Bettor.Id(0U), 3.2.toPounds(), 1.6.toOdds()))
            addOrder(Order.Back(Bettor.Id(0U), 2.8.toPounds(), 1.5.toOdds()))
        }

        assertEquals(
            emptyList<Pair<Odds, MutableList<Order.Lay>>>(),
            exchange.backs.toList()
        )

        assertEquals(
            listOf(1.6.toOdds() to listOf(Order.Lay(Bettor.Id(0U), 1.2.toPounds(), 1.6.toOdds()))),
            exchange.lays.toList()
        )
    }

    @Test
    fun recursiveMarketOrderPartialMatchBack() {
        val bettors = listOf(Bettor(Bettor.Id(0U), funds = 10.toPounds()))

        val exchange = Exchange(bettors).apply {
            addOrder(Order.Lay(Bettor.Id(0U), 0.3.toPounds(), 1.7.toOdds()))
            addOrder(Order.Lay(Bettor.Id(0U), 0.5.toPounds(), 1.9.toOdds()))
            addOrder(Order.Lay(Bettor.Id(0U), 1.1.toPounds(), 1.6.toOdds()))
            addOrder(Order.Back(Bettor.Id(0U), 2.8.toPounds(), 1.5.toOdds()))
        }

        assertEquals(
            listOf(1.5.toOdds() to listOf(Order.Back(Bettor.Id(0U), 0.9.toPounds(), 1.5.toOdds()))),
            exchange.backs.toList()
        )

        assertEquals(
            emptyList<Pair<Odds, MutableList<Order.Lay>>>(),
            exchange.lays.toList()
        )
    }

    @Test
    fun marketOrderMatchLay() {
        val bettors = listOf(Bettor(Bettor.Id(0U), funds = 10.toPounds()))

        val exchange = Exchange(bettors).apply {
            addOrder(Order.Back(Bettor.Id(0U), 1.toPounds(), 1.6.toOdds()))
            addOrder(Order.Back(Bettor.Id(0U), 1.toPounds(), 1.9.toOdds()))
            addOrder(Order.Lay(Bettor.Id(0U), 0.6.toPounds(), 1.8.toOdds()))
        }

        assertEquals(
            listOf(
                1.6.toOdds() to listOf(Order.Back(Bettor.Id(0U), 0.4.toPounds(), 1.6.toOdds())),
                1.9.toOdds() to listOf(Order.Back(Bettor.Id(0U), 1.toPounds(), 1.9.toOdds()))
            ),
            exchange.backs.toList()
        )

        assertEquals(
            emptyList<Pair<Odds, MutableList<Order.Lay>>>(),
            exchange.lays.toList()
        )
    }

    @Test
    fun recursiveMarketOrderMatchLay() {
        val bettors = listOf(Bettor(Bettor.Id(0U), funds = 10.toPounds()))

        val exchange = Exchange(bettors).apply {
            addOrder(Order.Back(Bettor.Id(0U), 0.3.toPounds(), 1.7.toOdds()))
            addOrder(Order.Back(Bettor.Id(0U), 3.2.toPounds(), 1.9.toOdds()))
            addOrder(Order.Back(Bettor.Id(0U), 0.5.toPounds(), 1.6.toOdds()))
            addOrder(Order.Lay(Bettor.Id(0U), 2.8.toPounds(), 2.toOdds()))
        }

        assertEquals(
            listOf(1.9.toOdds() to listOf(Order.Back(Bettor.Id(0U), 1.2.toPounds(), 1.9.toOdds()))),
            exchange.backs.toList()
        )

        assertEquals(
            emptyList<Pair<Odds, MutableList<Order.Lay>>>(),
            exchange.lays.toList()
        )
    }

    @Test
    fun recursiveMarketOrderPartialMatchLay() {
        val bettors = listOf(Bettor(Bettor.Id(0U), funds = 10.toPounds()))

        val exchange = Exchange(bettors).apply {
            addOrder(Order.Back(Bettor.Id(0U), 0.3.toPounds(), 1.7.toOdds()))
            addOrder(Order.Back(Bettor.Id(0U), 1.1.toPounds(), 1.9.toOdds()))
            addOrder(Order.Back(Bettor.Id(0U), 0.5.toPounds(), 1.6.toOdds()))
            addOrder(Order.Lay(Bettor.Id(0U), 2.8.toPounds(), 2.toOdds()))
        }

        assertEquals(
            emptyList<Pair<Odds, MutableList<Order.Back>>>(),
            exchange.backs.toList()
        )

        assertEquals(
            listOf(2.toOdds() to listOf(Order.Lay(Bettor.Id(0U), 0.9.toPounds(), 2.toOdds()))),
            exchange.lays.toList()
        )
    }

    @Test
    fun `Funds reduced after order`() {
        val exchange = Exchange(listOf(Bettor(Bettor.Id(0U), funds = 10.toPounds())))

        exchange.addOrder(Order.Back(Bettor.Id(0U), 0.5.toPounds(), 1.8.toOdds()))
        assertEquals(9.5.toPounds(), exchange.bettors[0].funds)

        exchange.addOrder(Order.Lay(Bettor.Id(0U), 0.5.toPounds(), 1.7.toOdds()))
        assertEquals(9.15.toPounds(), exchange.bettors[0].funds)
    }

    @Test
    fun `A bettor doesn't have enough funds to place order`() {
        // Throw NoeEnoughFundsException if out of funds
        val exchange = Exchange(listOf(Bettor(Bettor.Id(0U), funds = 10.toPounds())))

//        shouldThrow<NotEnoughFundsException> {
            exchange.addOrder(Order.Back(Bettor.Id(0U), 20.toPounds(), 1.7.toOdds()))
//        }

        assertEquals(10.toPounds(), exchange.bettors[0].funds)
    }

    @Test
    fun `Round money when greater than 2 decimal places`() {
        val order = Order.Lay(Bettor.Id(0U), 12.34.toPounds(), 1.23.toOdds())
        assertEquals(2.84.toPounds(), order.liability)

        val exchange = Exchange(listOf(Bettor(Bettor.Id(0U), funds = 10.toPounds()))).apply {
            addOrder(order)
        }

        assertEquals(7.16.toPounds(), exchange.bettors[0].funds)
    }
}
