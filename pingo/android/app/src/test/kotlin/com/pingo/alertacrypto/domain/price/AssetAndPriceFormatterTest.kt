package com.pingo.alertacrypto.domain.price

import com.pingo.alertacrypto.presentation.format.PriceFormatter
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AssetAndPriceFormatterTest {
    @Test
    fun supportedAssetsContainExactlyTheSixPlannedSymbols() {
        assertEquals(
            listOf("BTC", "ETH", "SOL", "XRP", "ADA", "DOGE"),
            Asset.supported.map(Asset::symbol),
        )
    }

    @Test
    fun priceFormatterUsesBrazilianCurrencyAndSeparators() {
        val formatted = PriceFormatter.brl(BigDecimal("1234.56"))

        assertTrue(formatted.startsWith("R$"))
        assertTrue(formatted.contains("1.234,56"))
    }

    @Test
    fun percentageFormatterUsesBrazilianDecimalSeparator() {
        assertEquals("1,25%", PriceFormatter.percent(BigDecimal("1.25")))
    }
}
