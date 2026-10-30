package com.pingo.alertacrypto.presentation.alerts

import com.pingo.alertacrypto.domain.alert.Alert
import com.pingo.alertacrypto.domain.alert.AlertDirection
import com.pingo.alertacrypto.domain.alert.RepeatMode
import com.pingo.alertacrypto.domain.price.PriceTick
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AlertCardProjectorTest {
    @Test
    fun stateUsesRequiredPrecedence() {
        assertEquals(AlertCardStatus.FIRED, AlertCardProjector.project(alert(repeat = RepeatMode.ONE_SHOT, armed = false, enabled = false), null).status)
        assertEquals(AlertCardStatus.PAUSED, AlertCardProjector.project(alert(armed = false, enabled = false), null).status)
        assertEquals(AlertCardStatus.WAITING_FOR_REARM, AlertCardProjector.project(alert(armed = false), null).status)
        assertEquals(AlertCardStatus.ACTIVE, AlertCardProjector.project(alert(), null).status)
    }

    @Test
    fun waitingCardShowsRearmSideAndThresholdUsingDomainHysteresis() {
        val upward = AlertCardProjector.project(alert(armed = false, firedUp = true), null)
        val downward = AlertCardProjector.project(alert(armed = false, firedUp = false), null)
        assertEquals("Reativa quando o preço voltar abaixo de R$ 99,90", upward.reactivationText)
        assertEquals("Reativa quando o preço voltar acima de R$ 100,10", downward.reactivationText)
    }

    @Test
    fun priceDistanceAndMissingPriceAreProjectedWithoutChangingAlert() {
        val alert = alert()
        val tick = PriceTick("BTC", BigDecimal("101"), BigDecimal("1.2"), Instant.EPOCH)
        val projected = AlertCardProjector.project(alert, tick)
        assertEquals("R$ 101,00", projected.currentPriceText)
        assertEquals("Distância: R$ 1,00", projected.distanceText)
        assertNull(AlertCardProjector.project(alert, null).currentPriceText)
        assertEquals(BigDecimal("100"), alert.targetPriceBRL)
    }

    private fun alert(
        repeat: RepeatMode = RepeatMode.REPEATING,
        armed: Boolean = true,
        enabled: Boolean = true,
        firedUp: Boolean? = null,
    ) = Alert(
        id = UUID.randomUUID(),
        symbol = "BTC",
        targetPriceBRL = BigDecimal("100"),
        direction = AlertDirection.BOTH,
        repeatMode = repeat,
        armed = armed,
        firedUp = firedUp,
        lastPriceBRL = null,
        enabled = enabled,
        createdAt = Instant.EPOCH,
        updatedAt = Instant.EPOCH,
        lastFiredAt = null,
        lastFiredTickAt = null,
    )
}
