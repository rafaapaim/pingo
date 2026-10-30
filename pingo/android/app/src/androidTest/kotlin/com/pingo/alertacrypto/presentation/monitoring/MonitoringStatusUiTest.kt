package com.pingo.alertacrypto.presentation.monitoring

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pingo.alertacrypto.domain.monitoring.QuoteFreshness
import com.pingo.alertacrypto.domain.monitoring.MonitoringState
import com.pingo.alertacrypto.domain.monitoring.MonitoringStatus
import com.pingo.alertacrypto.domain.price.Asset
import com.pingo.alertacrypto.domain.price.PriceTick
import com.pingo.alertacrypto.presentation.format.PriceFormatter
import com.pingo.alertacrypto.presentation.prices.AssetPrice
import com.pingo.alertacrypto.presentation.prices.PriceConnectionState
import com.pingo.alertacrypto.presentation.prices.PriceListUiState
import com.pingo.alertacrypto.presentation.theme.PingoTheme
import java.math.BigDecimal
import java.time.Instant
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MonitoringStatusUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun offlineAndSystemInterruptedStatesAreVisibleInPortuguese() {
        composeRule.setContent {
            PingoTheme(darkTheme = false) {
                MonitoringStatusBanner(
                    uiState = PriceListUiState(
                        connectionState = PriceConnectionState.OFFLINE,
                        monitoringState = MonitoringState(
                            monitoringStatus = MonitoringStatus.SYSTEM_INTERRUPTED,
                        ),
                    ),
                    onContinuousMonitoringChanged = {},
                    onRequestNotificationPermission = {},
                )
            }

        }

        composeRule.onNodeWithText("Sem conexão com a fonte de preços").assertIsDisplayed()
        composeRule.onNodeWithText(
            "O sistema interrompeu o monitoramento contínuo; verificações econômicas agendadas.",
        ).assertIsDisplayed()
    }

    @Test
    fun stalePriceStateIncludesLastUpdateInPortuguese() {
        val observedAt = Instant.parse("2026-01-01T12:34:56Z")
        val expected =
            "Cotação desatualizada · última atualização ${PriceFormatter.time(observedAt)}"
        composeRule.setContent {
            PingoTheme(darkTheme = false) {
                MonitoringStatusBanner(
                    uiState = PriceListUiState(
                        assets = listOf(
                            AssetPrice(
                                asset = Asset.BTC,
                                tick = PriceTick(
                                    symbol = "BTC",
                                    priceBRL = BigDecimal("1234.56"),
                                    change24hPercent = BigDecimal("1.25"),
                                    observedAt = observedAt,
                                ),
                                freshness = QuoteFreshness.STALE,
                            ),
                        ),
                    ),
                    onContinuousMonitoringChanged = {},
                    onRequestNotificationPermission = {},
                )
            }
        }

        composeRule.onNodeWithText(expected).assertIsDisplayed()
    }
}
