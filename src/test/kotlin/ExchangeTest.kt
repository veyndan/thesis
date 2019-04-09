import com.veyndan.thesis.exchange.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExchangeTest {

    @Test
    fun populate() {
        val exchange = Exchange().apply {
            addOrder(Order.Back(Trader(0), Price(1.toBigDecimal()), Odds.Back(1.8.toBigDecimal())))
            addOrder(Order.Lay(Trader(0), Price(1.toBigDecimal()), Odds.Lay(1.7.toBigDecimal())))
        }

        assertEquals(
            listOf(
                Odds.Back(1.8.toBigDecimal()) to listOf(
                    Order.Back(Trader(0), Price(1.toBigDecimal()), Odds.Back(1.8.toBigDecimal()))
                )
            ),
            exchange.backs.toList()
        )
        assertEquals(
            listOf(
                Odds.Lay(1.7.toBigDecimal()) to listOf(
                    Order.Lay(Trader(0), Price(1.toBigDecimal()), Odds.Lay(1.7.toBigDecimal()))
                )
            ),
            exchange.lays.toList()
        )
    }

    @Test
    fun populateMultipleBackOrdersAtSameOdds() {
        val exchange = Exchange().apply {
            addOrder(Order.Back(Trader(0), Price(1.toBigDecimal()), Odds.Back(1.7.toBigDecimal())))
            addOrder(Order.Back(Trader(1), Price(1.toBigDecimal()), Odds.Back(1.8.toBigDecimal())))
            addOrder(Order.Back(Trader(2), Price(1.toBigDecimal()), Odds.Back(1.7.toBigDecimal())))
        }

        assertEquals(
            listOf(
                Odds.Back(1.7.toBigDecimal()) to listOf(
                    Order.Back(Trader(0), Price(1.toBigDecimal()), Odds.Back(1.7.toBigDecimal())),
                    Order.Back(Trader(2), Price(1.toBigDecimal()), Odds.Back(1.7.toBigDecimal()))
                ),
                Odds.Back(1.8.toBigDecimal()) to listOf(
                    Order.Back(Trader(1), Price(1.toBigDecimal()), Odds.Back(1.8.toBigDecimal()))
                )
            ),
            exchange.backs.toList()
        )
    }

    @Test
    fun populateMultipleLayOrdersAtSameOdds() {
        val exchange = Exchange().apply {
            addOrder(Order.Lay(Trader(0), Price(1.toBigDecimal()), Odds.Lay(1.7.toBigDecimal())))
            addOrder(Order.Lay(Trader(1), Price(1.toBigDecimal()), Odds.Lay(1.8.toBigDecimal())))
            addOrder(Order.Lay(Trader(2), Price(1.toBigDecimal()), Odds.Lay(1.7.toBigDecimal())))
        }

        assertEquals(
            listOf(
                Odds.Lay(1.8.toBigDecimal()) to listOf(
                    Order.Lay(Trader(1), Price(1.toBigDecimal()), Odds.Lay(1.8.toBigDecimal()))
                ),
                Odds.Lay(1.7.toBigDecimal()) to listOf(
                    Order.Lay(Trader(0), Price(1.toBigDecimal()), Odds.Lay(1.7.toBigDecimal())),
                    Order.Lay(Trader(2), Price(1.toBigDecimal()), Odds.Lay(1.7.toBigDecimal()))
                )
            ),
            exchange.lays.toList()
        )
    }

    @Test
    fun orderedBacksWhenPopulating() {
        val exchange = Exchange().apply {
            addOrder(Order.Back(Trader(0), Price(1.toBigDecimal()), Odds.Back(2.2.toBigDecimal())))
            addOrder(Order.Back(Trader(0), Price(1.toBigDecimal()), Odds.Back(1.8.toBigDecimal())))
            addOrder(Order.Back(Trader(0), Price(1.toBigDecimal()), Odds.Back(1.7.toBigDecimal())))
        }

        assertEquals(
            listOf(
                Odds.Back(1.7.toBigDecimal()) to listOf(
                    Order.Back(Trader(0), Price(1.toBigDecimal()), Odds.Back(1.7.toBigDecimal()))
                ),
                Odds.Back(1.8.toBigDecimal()) to listOf(
                    Order.Back(Trader(0), Price(1.toBigDecimal()), Odds.Back(1.8.toBigDecimal()))
                ),
                Odds.Back(2.2.toBigDecimal()) to listOf(
                    Order.Back(Trader(0), Price(1.toBigDecimal()), Odds.Back(2.2.toBigDecimal()))
                )
            ),
            exchange.backs.toList()
        )
    }

    @Test
    fun orderedLaysWhenPopulating() {
        val exchange = Exchange().apply {
            addOrder(Order.Lay(Trader(0), Price(1.toBigDecimal()), Odds.Lay(1.8.toBigDecimal())))
            addOrder(Order.Lay(Trader(0), Price(1.toBigDecimal()), Odds.Lay(1.7.toBigDecimal())))
            addOrder(Order.Lay(Trader(0), Price(1.toBigDecimal()), Odds.Lay(2.2.toBigDecimal())))
        }

        assertEquals(
            listOf(
                Odds.Lay(2.2.toBigDecimal()) to listOf(
                    Order.Lay(Trader(0), Price(1.toBigDecimal()), Odds.Lay(2.2.toBigDecimal()))
                ),
                Odds.Lay(1.8.toBigDecimal()) to listOf(
                    Order.Lay(Trader(0), Price(1.toBigDecimal()), Odds.Lay(1.8.toBigDecimal()))
                ),
                Odds.Lay(1.7.toBigDecimal()) to listOf(
                    Order.Lay(Trader(0), Price(1.toBigDecimal()), Odds.Lay(1.7.toBigDecimal()))
                )
            ),
            exchange.lays.toList()
        )
    }

    @Test
    fun deleteLoneBackOrder() {
        val exchange = Exchange().apply {
            addOrder(Order.Back(Trader(0), Price(1.toBigDecimal()), Odds.Back(1.8.toBigDecimal())))
            addOrder(Order.Back(Trader(0), Price(1.toBigDecimal()), Odds.Back(1.7.toBigDecimal())))
        }

        exchange.deleteOrder(Order.Back(Trader(0), Price(1.toBigDecimal()), Odds.Back(1.8.toBigDecimal())))

        assertEquals(
            listOf(
                Odds.Back(1.7.toBigDecimal()) to listOf(
                    Order.Back(Trader(0), Price(1.toBigDecimal()), Odds.Back(1.7.toBigDecimal()))
                )
            ),
            exchange.backs.toList()
        )
    }

    @Test
    fun marketOrderSamePricedBack() {
        val exchange = Exchange().apply {
            addOrder(Order.Lay(Trader(0), Price(1.toBigDecimal()), Odds.Lay(1.7.toBigDecimal())))
            addOrder(Order.Lay(Trader(0), Price(1.toBigDecimal()), Odds.Lay(1.9.toBigDecimal())))
            addOrder(Order.Back(Trader(0), Price(1.toBigDecimal()), Odds.Back(1.8.toBigDecimal())))
        }

        assertEquals(
            emptyList<Pair<Odds.Lay, MutableList<Order.Lay>>>(),
            exchange.backs.toList()
        )

        assertEquals(
            listOf(
                Odds.Lay(1.7.toBigDecimal()) to listOf(
                    Order.Lay(Trader(0), Price(1.toBigDecimal()), Odds.Lay(1.7.toBigDecimal()))
                )
            ),
            exchange.lays.toList()
        )
    }

    @Test
    fun marketOrderSamePricedLay() {
        val exchange = Exchange().apply {
            addOrder(Order.Back(Trader(0), Price(1.toBigDecimal()), Odds.Back(1.7.toBigDecimal())))
            addOrder(Order.Back(Trader(0), Price(1.toBigDecimal()), Odds.Back(1.9.toBigDecimal())))
            addOrder(Order.Lay(Trader(0), Price(1.toBigDecimal()), Odds.Lay(1.8.toBigDecimal())))
        }

        assertEquals(
            listOf(
                Odds.Back(1.9.toBigDecimal()) to listOf(
                    Order.Back(Trader(0), Price(1.toBigDecimal()), Odds.Back(1.9.toBigDecimal()))
                )
            ),
            exchange.backs.toList()
        )

        assertEquals(emptyList<Pair<Odds.Lay, MutableList<Order.Lay>>>(), exchange.lays.toList())
    }

    @Test
    fun marketOrderPartialMatchThenLimitOrderBack() {
        val exchange = Exchange().apply {
            addOrder(Order.Lay(Trader(0), Price(1.toBigDecimal()), Odds.Lay(1.7.toBigDecimal())))
            addOrder(Order.Lay(Trader(0), Price(1.toBigDecimal()), Odds.Lay(1.9.toBigDecimal())))
            addOrder(Order.Back(Trader(0), Price(0.6.toBigDecimal()), Odds.Back(1.8.toBigDecimal())))
        }

        assertEquals(
            emptyList<Pair<Odds.Lay, MutableList<Order.Lay>>>(),
            exchange.backs.toList()
        )

        assertEquals(
            listOf(
                Odds.Lay(1.9.toBigDecimal()) to listOf(
                    Order.Lay(Trader(0), Price(0.4.toBigDecimal()), Odds.Lay(1.9.toBigDecimal()))
                ),
                Odds.Lay(1.7.toBigDecimal()) to listOf(
                    Order.Lay(Trader(0), Price(1.toBigDecimal()), Odds.Lay(1.7.toBigDecimal()))
                )
            ),
            exchange.lays.toList()
        )
    }
}
