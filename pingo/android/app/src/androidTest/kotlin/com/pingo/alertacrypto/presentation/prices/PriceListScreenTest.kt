package com.pingo.alertacrypto.presentation.prices

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pingo.alertacrypto.domain.monitoring.QuoteFreshness
import com.pingo.alertacrypto.domain.price.PriceTick
import com.pingo.alertacrypto.presentation.format.PriceFormatter
import com.pingo.alertacrypto.domain.price.Asset
import com.pingo.alertacrypto.presentation.theme.PingoTheme
import java.math.BigDecimal
import java.time.Instant
import java.util.concurrent.atomic.AtomicReference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PriceListScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun rendersSixAssetsAndSearchFiltersByName() {
        composeRule.setContent {
            PingoTheme(darkTheme = false) {
                PriceListContent(
                    uiState = PriceListUiState(connectionState = PriceConnectionState.ONLINE),
                    onOpenAsset = {},
                    onFavorite = { _, _ -> },
                    onOpenSettings = {},
                )
            }
        }

        composeRule.onNodeWithText("Moedas").assertIsDisplayed()
        Asset.supported.forEach { asset ->
            composeRule.onNodeWithTag("market_assets")
                .performScrollToNode(hasText(asset.symbol))
            composeRule.onNodeWithText(asset.symbol).assertIsDisplayed()
        }
        composeRule.onNodeWithTag("market_search").performTextInput("Solana")
        composeRule.onNodeWithText("Solana").assertIsDisplayed()
        composeRule.runOnIdle {
            assertTrue(
                composeRule.onAllNodesWithText("Bitcoin").fetchSemanticsNodes().isEmpty(),
            )
        }
    }

    @Test
    fun favoriteControlProvidesAccessibleAction() {
        val favorited = AtomicReference<String?>(null)
        composeRule.setContent {
            PingoTheme(darkTheme = false) {
                PriceListContent(
                    uiState = PriceListUiState(connectionState = PriceConnectionState.ONLINE),
                    onOpenAsset = {},
                    onFavorite = { symbol, isFavorite ->
                        if (isFavorite) favorited.set(symbol)
                    },
                    onOpenSettings = {},
                )
            }
        }

        composeRule.onNodeWithContentDescription("Adicionar Bitcoin aos favoritos").performClick()
        composeRule.runOnIdle {
            assertEquals(Asset.BTC.symbol, favorited.get())
        }
    }

    @Test
    fun showsCompactGlobalStatusWithoutMonitoringControlsOrRepeatedRowTimestamps() {
        val observedAt = Instant.parse("2026-10-30T12:00:00Z")
        val sharedTick = PriceTick(
            symbol = "BTC",
            priceBRL = BigDecimal("350000.00"),
            change24hPercent = BigDecimal("1.25"),
            observedAt = observedAt,
        )
        composeRule.setContent {
            PingoTheme(darkTheme = false) {
                PriceListContent(
                    uiState = PriceListUiState(
                        assets = Asset.supported.map { asset ->
                            AssetPrice(
                                asset = asset,
                                tick = sharedTick.copy(symbol = asset.symbol),
                                freshness = QuoteFreshness.FRESH,
                            )
                        },
                        connectionState = PriceConnectionState.ONLINE,
                    ),
                    onOpenAsset = {},
                    onFavorite = { _, _ -> },
                    onOpenSettings = {},
                )
            }
        }

        composeRule.onNodeWithText("Online").assertIsDisplayed()
        composeRule.onNodeWithText("Monitoramento · Pausado").assertIsDisplayed()
        composeRule.runOnIdle {
            assertTrue(
                composeRule.onAllNodesWithText("Monitoramento contínuo")
                    .fetchSemanticsNodes().isEmpty(),
            )
        }
        composeRule.runOnIdle {
            assertEquals(
                1,
                composeRule.onAllNodesWithText(
                    "Atualizado às ${PriceFormatter.time(observedAt)}",
                    substring = false,
                ).fetchSemanticsNodes().size,
            )
        }
    }

    @Test
    fun monitoringStatusChipOpensSettings() {
        val settingsOpened = AtomicReference(false)
        composeRule.setContent {
            PingoTheme(darkTheme = false) {
                PriceListContent(
                    uiState = PriceListUiState(connectionState = PriceConnectionState.ONLINE),
                    onOpenAsset = {},
                    onFavorite = { _, _ -> },
                    onOpenSettings = { settingsOpened.set(true) },
                )
            }
        }

        composeRule.onNodeWithTag("market_monitoring_settings").performClick()
        composeRule.runOnIdle {
            assertTrue(settingsOpened.get())
        }
    }
}
