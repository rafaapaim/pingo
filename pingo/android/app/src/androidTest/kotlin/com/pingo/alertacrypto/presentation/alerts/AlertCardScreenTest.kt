package com.pingo.alertacrypto.presentation.alerts

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pingo.alertacrypto.domain.alert.Alert
import com.pingo.alertacrypto.domain.alert.AlertDirection
import com.pingo.alertacrypto.domain.alert.RepeatMode
import com.pingo.alertacrypto.domain.monitoring.QuoteFreshness
import com.pingo.alertacrypto.domain.price.PriceTick
import com.pingo.alertacrypto.presentation.theme.PingoTheme
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlertCardScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun waitingCardShowsLocalizedPriceDistanceAndRearmDirection() {
        val alert = Alert(
            id = UUID.fromString("00000000-0000-0000-0000-000000000001"),
            symbol = "BTC",
            targetPriceBRL = BigDecimal("100"),
            direction = AlertDirection.BOTH,
            repeatMode = RepeatMode.REPEATING,
            armed = false,
            firedUp = true,
            lastPriceBRL = BigDecimal("101"),
            enabled = true,
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH,
            lastFiredAt = Instant.EPOCH,
            lastFiredTickAt = Instant.EPOCH,
        )
        val model = AlertCardProjector.project(
            alert = alert,
            tick = PriceTick("BTC", BigDecimal("101"), BigDecimal("1.25"), Instant.EPOCH),
            quoteFreshness = QuoteFreshness.STALE,
        )
        composeRule.setContent {
            PingoTheme(darkTheme = false) {
                AlertCard(model)
            }
        }

        listOf(
            "Aguardando rearme",
            "Última cotação: R$ 101,00",
            "Distância: R$ 1,00",
            "Reativa quando o preço voltar abaixo de R$ 99,90",
        ).forEach { composeRule.onNodeWithText(it).assertIsDisplayed() }
    }
}
