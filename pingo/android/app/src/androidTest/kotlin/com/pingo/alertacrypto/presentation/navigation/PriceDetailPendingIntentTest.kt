package com.pingo.alertacrypto.presentation.navigation

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PriceDetailPendingIntentTest {
    @Test
    fun notificationPendingIntentsRemainDistinctByAssetAndUseCorrectDeepLinks() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val btcIntent = PriceDetailDestination.intent(context, "BTC")
        val ethIntent = PriceDetailDestination.intent(context, "ETH")
        val btcPendingIntent = PriceDetailDestination.pendingIntent(context, "BTC")
        val ethPendingIntent = PriceDetailDestination.pendingIntent(context, "ETH")

        assertEquals("pingo://asset/BTC", btcIntent.dataString)
        assertEquals("BTC", btcIntent.getStringExtra(PriceDetailDestination.EXTRA_SYMBOL))
        assertEquals("pingo://asset/ETH", ethIntent.dataString)
        assertEquals("ETH", ethIntent.getStringExtra(PriceDetailDestination.EXTRA_SYMBOL))
        assertNotEquals(btcPendingIntent, ethPendingIntent)
        assertTrue(btcPendingIntent.isImmutable)
        assertTrue(ethPendingIntent.isImmutable)
    }
}
