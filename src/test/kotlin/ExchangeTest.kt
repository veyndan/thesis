@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

import com.veyndan.thesis.exchange.*
import com.veyndan.thesis.toPounds
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExchangeTest {

    @Test
    fun populate() {
        val exchange = Exchange().apply {
            addOrder(Order.Back(Bettor(id = 0U, funds = 10.toPounds()), 1.toPounds(), 1.8.toOdds()))
            addOrder(Order.Lay(Bettor(id = 0U, funds = 10.toPounds()), 1.toPounds(), 1.7.toOdds()))
        }

        assertEquals(
            listOf(
                1.8.toOdds() to listOf(
                    Order.Back(
                        Bettor(id = 0U, funds = 9.toPounds()),
                        1.toPounds(),
                        1.8.toOdds()
                    )
                )
            ),
            exchange.backs.toList()
        )
        assertEquals(
            listOf(
                1.7.toOdds() to listOf(
                    Order.Lay(
                        Bettor(id = 0U, funds = 9.toPounds()),
                        1.toPounds(),
                        1.7.toOdds()
                    )
                )
            ),
            exchange.lays.toList()
        )
    }

    @Test
    fun populateMultipleBackOrdersAtSameOdds() {
        val exchange = Exchange().apply {
            addOrder(
                Order.Back(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    1.toPounds(),
                    1.7.toOdds()
                )
            )
            addOrder(
                Order.Back(
                    Bettor(id = 1U, funds = 10.toPounds()),
                    1.toPounds(),
                    1.8.toOdds()
                )
            )
            addOrder(
                Order.Back(
                    Bettor(id = 2U, funds = 10.toPounds()),
                    1.toPounds(),
                    1.7.toOdds()
                )
            )
        }

        assertEquals(
            listOf(
                1.7.toOdds() to listOf(
                    Order.Back(
                        Bettor(id = 0U, funds = 10.toPounds()),
                        1.toPounds(),
                        1.7.toOdds()
                    ),
                    Order.Back(
                        Bettor(id = 2U, funds = 10.toPounds()),
                        1.toPounds(),
                        1.7.toOdds()
                    )
                ),
                1.8.toOdds() to listOf(
                    Order.Back(
                        Bettor(id = 1U, funds = 10.toPounds()),
                        1.toPounds(),
                        1.8.toOdds()
                    )
                )
            ),
            exchange.backs.toList()
        )
    }

    @Test
    fun populateMultipleLayOrdersAtSameOdds() {
        val exchange = Exchange().apply {
            addOrder(
                Order.Lay(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    1.toPounds(),
                    1.7.toOdds()
                )
            )
            addOrder(
                Order.Lay(
                    Bettor(id = 1U, funds = 10.toPounds()),
                    1.toPounds(),
                    1.8.toOdds()
                )
            )
            addOrder(
                Order.Lay(
                    Bettor(id = 2U, funds = 10.toPounds()),
                    1.toPounds(),
                    1.7.toOdds()
                )
            )
        }

        assertEquals(
            listOf(
                1.8.toOdds() to listOf(
                    Order.Lay(
                        Bettor(id = 1U, funds = 10.toPounds()),
                        1.toPounds(),
                        1.8.toOdds()
                    )
                ),
                1.7.toOdds() to listOf(
                    Order.Lay(
                        Bettor(id = 0U, funds = 10.toPounds()),
                        1.toPounds(),
                        1.7.toOdds()
                    ),
                    Order.Lay(
                        Bettor(id = 2U, funds = 10.toPounds()),
                        1.toPounds(),
                        1.7.toOdds()
                    )
                )
            ),
            exchange.lays.toList()
        )
    }

    @Test
    fun orderedBacksWhenPopulating() {
        val exchange = Exchange().apply {
            addOrder(
                Order.Back(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    1.toPounds(),
                    2.2.toOdds()
                )
            )
            addOrder(
                Order.Back(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    1.toPounds(),
                    1.8.toOdds()
                )
            )
            addOrder(
                Order.Back(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    1.toPounds(),
                    1.7.toOdds()
                )
            )
        }

        assertEquals(
            listOf(
                1.7.toOdds() to listOf(
                    Order.Back(
                        Bettor(id = 0U, funds = 10.toPounds()),
                        1.toPounds(),
                        1.7.toOdds()
                    )
                ),
                1.8.toOdds() to listOf(
                    Order.Back(
                        Bettor(id = 0U, funds = 10.toPounds()),
                        1.toPounds(),
                        1.8.toOdds()
                    )
                ),
                2.2.toOdds() to listOf(
                    Order.Back(
                        Bettor(id = 0U, funds = 10.toPounds()),
                        1.toPounds(),
                        2.2.toOdds()
                    )
                )
            ),
            exchange.backs.toList()
        )
    }

    @Test
    fun orderedLaysWhenPopulating() {
        val exchange = Exchange().apply {
            addOrder(
                Order.Lay(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    1.toPounds(),
                    1.8.toOdds()
                )
            )
            addOrder(
                Order.Lay(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    1.toPounds(),
                    1.7.toOdds()
                )
            )
            addOrder(
                Order.Lay(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    1.toPounds(),
                    2.2.toOdds()
                )
            )
        }

        assertEquals(
            listOf(
                2.2.toOdds() to listOf(
                    Order.Lay(
                        Bettor(id = 0U, funds = 10.toPounds()),
                        1.toPounds(),
                        2.2.toOdds()
                    )
                ),
                1.8.toOdds() to listOf(
                    Order.Lay(
                        Bettor(id = 0U, funds = 10.toPounds()),
                        1.toPounds(),
                        1.8.toOdds()
                    )
                ),
                1.7.toOdds() to listOf(
                    Order.Lay(
                        Bettor(id = 0U, funds = 10.toPounds()),
                        1.toPounds(),
                        1.7.toOdds()
                    )
                )
            ),
            exchange.lays.toList()
        )
    }

    @Test
    fun `A bettor cancels a back order`() {
        val exchange = Exchange().apply {
            addOrder(
                Order.Back(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    1.toPounds(),
                    1.8.toOdds()
                )
            )
            addOrder(
                Order.Back(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    1.toPounds(),
                    1.7.toOdds()
                )
            )
        }

        exchange.deleteOrder(
            Order.Back(
                Bettor(id = 0U, funds = 10.toPounds()),
                1.toPounds(),
                1.8.toOdds()
            )
        )

        assertEquals(
            listOf(
                1.7.toOdds() to listOf(
                    Order.Back(
                        Bettor(id = 0U, funds = 10.toPounds()),
                        1.toPounds(),
                        1.7.toOdds()
                    )
                )
            ),
            exchange.backs.toList()
        )
    }

    @Test
    fun marketOrderSamePricedBack() {
        val exchange = Exchange().apply {
            addOrder(
                Order.Lay(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    1.toPounds(),
                    1.7.toOdds()
                )
            )
            addOrder(
                Order.Lay(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    1.toPounds(),
                    1.9.toOdds()
                )
            )
            addOrder(
                Order.Back(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    1.toPounds(),
                    1.8.toOdds()
                )
            )
        }

        assertEquals(
            emptyList<Pair<Odds, MutableList<Order.Lay>>>(),
            exchange.backs.toList()
        )

        assertEquals(
            listOf(
                1.7.toOdds() to listOf(
                    Order.Lay(
                        Bettor(id = 0U, funds = 10.toPounds()),
                        1.toPounds(),
                        1.7.toOdds()
                    )
                )
            ),
            exchange.lays.toList()
        )
    }

    @Test
    fun marketOrderSamePricedLay() {
        val exchange = Exchange().apply {
            addOrder(
                Order.Back(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    1.toPounds(),
                    1.7.toOdds()
                )
            )
            addOrder(
                Order.Back(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    1.toPounds(),
                    1.9.toOdds()
                )
            )
            addOrder(
                Order.Lay(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    1.toPounds(),
                    1.8.toOdds()
                )
            )
        }

        assertEquals(
            listOf(
                1.9.toOdds() to listOf(
                    Order.Back(
                        Bettor(id = 0U, funds = 10.toPounds()),
                        1.toPounds(),
                        1.9.toOdds()
                    )
                )
            ),
            exchange.backs.toList()
        )

        assertEquals(emptyList<Pair<Odds, MutableList<Order.Lay>>>(), exchange.lays.toList())
    }

    @Test
    fun marketOrderMatchBack() {
        val exchange = Exchange().apply {
            addOrder(
                Order.Lay(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    1.toPounds(),
                    1.7.toOdds()
                )
            )
            addOrder(
                Order.Lay(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    1.toPounds(),
                    1.9.toOdds()
                )
            )
            addOrder(
                Order.Back(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    0.6.toPounds(),
                    1.8.toOdds()
                )
            )
        }

        assertEquals(
            emptyList<Pair<Odds, MutableList<Order.Lay>>>(),
            exchange.backs.toList()
        )

        assertEquals(
            listOf(
                1.9.toOdds() to listOf(
                    Order.Lay(
                        Bettor(id = 0U, funds = 10.toPounds()),
                        0.4.toPounds(),
                        1.9.toOdds()
                    )
                ),
                1.7.toOdds() to listOf(
                    Order.Lay(
                        Bettor(id = 0U, funds = 10.toPounds()),
                        1.toPounds(),
                        1.7.toOdds()
                    )
                )
            ),
            exchange.lays.toList()
        )
    }

    @Test
    fun recursiveMarketOrderMatchBack() {
        val exchange = Exchange().apply {
            addOrder(
                Order.Lay(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    0.3.toPounds(),
                    1.7.toOdds()
                )
            )
            addOrder(
                Order.Lay(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    0.5.toPounds(),
                    1.9.toOdds()
                )
            )
            addOrder(
                Order.Lay(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    3.2.toPounds(),
                    1.6.toOdds()
                )
            )
            addOrder(
                Order.Back(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    2.8.toPounds(),
                    1.5.toOdds()
                )
            )
        }

        assertEquals(
            emptyList<Pair<Odds, MutableList<Order.Lay>>>(),
            exchange.backs.toList()
        )

        assertEquals(
            listOf(
                1.6.toOdds() to listOf(
                    Order.Lay(
                        Bettor(id = 0U, funds = 10.toPounds()),
                        1.2.toPounds(),
                        1.6.toOdds()
                    )
                )
            ),
            exchange.lays.toList()
        )
    }

    @Test
    fun recursiveMarketOrderPartialMatchBack() {
        val exchange = Exchange().apply {
            addOrder(
                Order.Lay(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    0.3.toPounds(),
                    1.7.toOdds()
                )
            )
            addOrder(
                Order.Lay(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    0.5.toPounds(),
                    1.9.toOdds()
                )
            )
            addOrder(
                Order.Lay(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    1.1.toPounds(),
                    1.6.toOdds()
                )
            )
            addOrder(
                Order.Back(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    2.8.toPounds(),
                    1.5.toOdds()
                )
            )
        }

        assertEquals(
            listOf(
                1.5.toOdds() to listOf(
                    Order.Back(
                        Bettor(id = 0U, funds = 10.toPounds()),
                        0.9.toPounds(),
                        1.5.toOdds()
                    )
                )
            ),
            exchange.backs.toList()
        )

        assertEquals(
            emptyList<Pair<Odds, MutableList<Order.Lay>>>(),
            exchange.lays.toList()
        )
    }

    @Test
    fun marketOrderMatchLay() {
        val exchange = Exchange().apply {
            addOrder(
                Order.Back(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    1.toPounds(),
                    1.6.toOdds()
                )
            )
            addOrder(
                Order.Back(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    1.toPounds(),
                    1.9.toOdds()
                )
            )
            addOrder(
                Order.Lay(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    0.6.toPounds(),
                    1.8.toOdds()
                )
            )
        }

        assertEquals(
            listOf(
                1.6.toOdds() to listOf(
                    Order.Back(
                        Bettor(id = 0U, funds = 10.toPounds()),
                        0.4.toPounds(),
                        1.6.toOdds()
                    )
                ),
                1.9.toOdds() to listOf(
                    Order.Back(
                        Bettor(id = 0U, funds = 10.toPounds()),
                        1.toPounds(),
                        1.9.toOdds()
                    )
                )
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
        val exchange = Exchange().apply {
            addOrder(
                Order.Back(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    0.3.toPounds(),
                    1.7.toOdds()
                )
            )
            addOrder(
                Order.Back(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    3.2.toPounds(),
                    1.9.toOdds()
                )
            )
            addOrder(
                Order.Back(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    0.5.toPounds(),
                    1.6.toOdds()
                )
            )
            addOrder(
                Order.Lay(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    2.8.toPounds(),
                    2.toOdds()
                )
            )
        }

        assertEquals(
            listOf(
                1.9.toOdds() to listOf(
                    Order.Back(
                        Bettor(id = 0U, funds = 10.toPounds()),
                        1.2.toPounds(),
                        1.9.toOdds()
                    )
                )
            ),
            exchange.backs.toList()
        )

        assertEquals(
            emptyList<Pair<Odds, MutableList<Order.Lay>>>(),
            exchange.lays.toList()
        )
    }

    @Test
    fun recursiveMarketOrderPartialMatchLay() {
        val exchange = Exchange().apply {
            addOrder(
                Order.Back(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    0.3.toPounds(),
                    1.7.toOdds()
                )
            )
            addOrder(
                Order.Back(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    1.1.toPounds(),
                    1.9.toOdds()
                )
            )
            addOrder(
                Order.Back(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    0.5.toPounds(),
                    1.6.toOdds()
                )
            )
            addOrder(
                Order.Lay(
                    Bettor(id = 0U, funds = 10.toPounds()),
                    2.8.toPounds(),
                    2.toOdds()
                )
            )
        }

        assertEquals(
            emptyList<Pair<Odds, MutableList<Order.Back>>>(),
            exchange.backs.toList()
        )

        assertEquals(
            listOf(
                2.toOdds() to listOf(
                    Order.Lay(
                        Bettor(id = 0U, funds = 10.toPounds()),
                        0.9.toPounds(),
                        2.toOdds()
                    )
                )
            ),
            exchange.lays.toList()
        )
    }

    @Test
    fun `A bettor doesn't have enough funds to place order`() {
        assert(false)
    }
}
