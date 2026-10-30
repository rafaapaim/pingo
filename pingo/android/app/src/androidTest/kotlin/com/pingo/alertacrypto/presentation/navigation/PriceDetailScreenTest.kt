package com.pingo.alertacrypto.presentation.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pingo.alertacrypto.domain.monitoring.QuoteFreshness
import com.pingo.alertacrypto.domain.price.Asset
import com.pingo.alertacrypto.domain.price.PriceTick
import com.pingo.alertacrypto.presentation.prices.AssetPrice
import com.pingo.alertacrypto.presentation.theme.PingoTheme
import java.math.BigDecimal
import java.time.Instant
import java.util.concurrent.atomic.AtomicBoolean
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PriceDetailScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsOnlyAllowedAssetDetailsAndOpensAlertCreation() {
        val createAlertClicked = AtomicBoolean(false)
        composeRule.setContent {
            PingoTheme(darkTheme = false) {
                PriceDetailContent(
                    asset = Asset.BTC,
                    price = AssetPrice(
                        asset = Asset.BTC,
                        tick = PriceTick(
                            symbol = "BTC",
                            priceBRL = BigDecimal("1234.56"),
                            change24hPercent = BigDecimal("1.25"),
                            observedAt = Instant.parse("2026-01-01T12:34:56Z"),
                        ),
                        freshness = QuoteFreshness.FRESH,
                    ),
                    onCreateAlert = { createAlertClicked.set(true) },
                )
            }
        }

        listOf("Bitcoin", "BTC", "R$ 1.234,56", "1,25%", "Criar alerta")
            .forEach { composeRule.onNodeWithText(it).assertIsDisplayed() }
        composeRule.onNodeWithText("Criar alerta").performClick()
        composeRule.runOnIdle { assertTrue(createAlertClicked.get()) }
    }

    @Test
    fun missingQuoteIsNotPresentedAsAnAvailablePrice() {
        composeRule.setContent {
            PingoTheme(darkTheme = true) {
                PriceDetailContent(
                    asset = Asset.ETH,
                    price = null,
                    onCreateAlert = {},
                )
            }
        }

        composeRule.onNodeWithText("Ethereum").assertIsDisplayed()
        composeRule.onNodeWithText("Cotação indisponível").assertIsDisplayed()
    }
}
