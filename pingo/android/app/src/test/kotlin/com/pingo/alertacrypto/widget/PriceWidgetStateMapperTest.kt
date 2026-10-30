package com.pingo.alertacrypto.widget

import com.pingo.alertacrypto.domain.monitoring.QuoteFreshness
import com.pingo.alertacrypto.domain.price.PriceTick
import java.math.BigDecimal
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PriceWidgetStateMapperTest {
    @Test
    fun mapsFreshTickWithBrazilianCurrencyVariationAndTime() {
        val state = PriceWidgetStateMapper.map(
            tick = tick(),
            receivedAtMillis = 0L,
            nowMillis = 1_800_000L,
        )

        assertEquals("BTC", state.symbol)
        assertEquals("R$ 1.234,56", state.priceText)
        assertEquals("1,25%", state.changeText)
        assertTrue(state.priceText.startsWith("R$ "))
        assertTrue(state.priceText.contains(','))
        assertTrue(state.statusText.isEmpty())
        assertTrue(state.updatedAtText.matches(Regex("\\d{2}:\\d{2}:\\d{2}")))
        assertEquals(QuoteFreshness.FRESH, state.freshness)
        assertTrue(state.showPrice)
    }

    @Test
    fun staleTickIsMarkedAndNotExposedAsCurrentPrice() {
        val state = PriceWidgetStateMapper.map(
            tick = tick(),
            receivedAtMillis = 0L,
            nowMillis = 1_800_001L,
        )

        assertEquals(QuoteFreshness.STALE, state.freshness)
        assertFalse(state.showPrice)
        assertEquals("Cotação desatualizada", state.statusText)
        assertEquals("R$ 1.234,56", state.priceText)
        assertTrue(state.statusText.matches(Regex("[\\p{L} ]+")))
    }

    @Test
    fun missingTickProducesUnavailableStateWithoutPrice() {
        val state = PriceWidgetStateMapper.map(
            tick = null,
            receivedAtMillis = null,
            nowMillis = 10L,
            selectedSymbol = "ETH",
        )

        assertEquals(QuoteFreshness.MISSING, state.freshness)
        assertFalse(state.showPrice)
        assertEquals("--", state.priceText)
        assertEquals("Cotação indisponível", state.statusText)
        assertTrue(state.statusText.matches(Regex("[\\p{L} ]+")))
        assertEquals("ETH", state.symbol)
        assertEquals("", state.changeText)
        assertEquals("", state.updatedAtText)
    }

    private fun tick() = PriceTick(
        symbol = "BTC",
        priceBRL = BigDecimal("1234.56"),
        change24hPercent = BigDecimal("1.25"),
        observedAt = Instant.parse("2026-01-01T00:00:00Z"),
    )
}
