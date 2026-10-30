package com.pingo.alertacrypto.presentation.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PingoRoutesTest {
    @Test
    fun resolvesSupportedDeepLinkAndExtraSymbolsCaseInsensitively() {
        assertEquals("BTC", PriceDetailDeepLink.resolve("pingo://asset/btc"))
        assertEquals("DOGE", PriceDetailDeepLink.resolve(null, "doge"))
    }

    @Test
    fun rejectsMalformedUnknownAndUnexpectedDeepLinks() {
        listOf(
            "https://asset/BTC",
            "pingo://asset/UNKNOWN",
            "pingo://asset/BTC/ETH",
            "pingo://other/BTC",
            "not a uri",
        ).forEach { assertNull(PriceDetailDeepLink.resolve(it)) }
    }

    @Test
    fun detailRoutesEncodeSymbolsAndOptionalEditorArguments() {
        assertEquals("asset/BTC", PingoRoutes.assetDetails("BTC"))
        assertEquals("alert-editor?alertId=&symbol=ETH", PingoRoutes.alertEditor(symbol = "ETH"))
    }
}
