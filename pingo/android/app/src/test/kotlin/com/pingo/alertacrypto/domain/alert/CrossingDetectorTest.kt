package com.pingo.alertacrypto.domain.alert

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CrossingDetectorTest {
    private var tickNumber = 0L

    @Test
    fun detectsStrictUpwardCrossing() {
        val result = evaluate(
            alert(direction = AlertDirection.UP, lastPrice = "100"),
            "100.01",
        )

        assertEquals(true, result.firedDirectionUp)
        assertFalse(result.alert.armed)
        assertEquals(true, result.alert.firedUp)
    }

    @Test
    fun detectsStrictDownwardCrossing() {
        val result = evaluate(
            alert(direction = AlertDirection.DOWN, lastPrice = "100"),
            "99.99",
        )

        assertEquals(false, result.firedDirectionUp)
        assertFalse(result.alert.armed)
        assertEquals(false, result.alert.firedUp)
    }

    @Test
    fun upwardOnlyAlertIgnoresDownwardCrossing() {
        val result = evaluate(
            alert(direction = AlertDirection.UP, lastPrice = "101"),
            "99",
        )

        assertNull(result.firedDirectionUp)
        assertTrue(result.alert.armed)
    }

    @Test
    fun bothDirectionAlertCanFireOnDownwardCrossing() {
        val result = evaluate(alert(lastPrice = "100"), "99")

        assertEquals(false, result.firedDirectionUp)
        assertEquals(false, result.alert.firedUp)
        assertFalse(result.alert.armed)
    }

    @Test
    fun bothDirectionPersistsEachFiredSideAcrossOppositeRearmAndNextCrossing() {
        val firedUp = evaluate(alert(lastPrice = "100"), "101")
        assertEquals(true, firedUp.firedDirectionUp)
        assertEquals(true, firedUp.alert.firedUp)
        assertFalse(firedUp.alert.armed)

        val rearmedBelow = evaluate(firedUp.alert, "99.90")
        assertNull(rearmedBelow.firedDirectionUp)
        assertTrue(rearmedBelow.alert.armed)
        assertEquals(true, rearmedBelow.alert.firedUp)

        val firedUpAgain = evaluate(rearmedBelow.alert, "101")
        assertEquals(true, firedUpAgain.firedDirectionUp)
        assertEquals(true, firedUpAgain.alert.firedUp)

        val firedDown = evaluate(alert(lastPrice = "101"), "99")
        assertEquals(false, firedDown.firedDirectionUp)
        assertEquals(false, firedDown.alert.firedUp)
        assertFalse(firedDown.alert.armed)

        val rearmedAbove = evaluate(firedDown.alert, "100.10")
        assertNull(rearmedAbove.firedDirectionUp)
        assertTrue(rearmedAbove.alert.armed)
        assertEquals(false, rearmedAbove.alert.firedUp)

        val firedDownAgain = evaluate(rearmedAbove.alert, "99")
        assertEquals(false, firedDownAgain.firedDirectionUp)
        assertEquals(false, firedDownAgain.alert.firedUp)
    }

    @Test
    fun downOnlyAlertIgnoresUpwardCrossing() {
        val result = evaluate(
            alert(direction = AlertDirection.DOWN, lastPrice = "99"),
            "101",
        )

        assertNull(result.firedDirectionUp)
        assertTrue(result.alert.armed)
        assertNull(result.alert.firedUp)
    }

    @Test
    fun touchingTargetDoesNotCountAsCrossing() {
        val result = evaluate(alert(lastPrice = "99"), "100")

        assertNull(result.firedDirectionUp)
        assertTrue(result.alert.armed)
    }

    @Test
    fun firstPriceInitializesReferenceWithoutRetrospectiveCrossing() {
        val result = evaluate(alert(), "101")

        assertNull(result.firedDirectionUp)
        assertEquals(BigDecimal("101"), result.alert.lastPriceBRL)
        assertTrue(result.alert.armed)
    }

    @Test
    fun nonPositivePriceIsRejectedWithoutChangingAlert() {
        val original = alert(lastPrice = "99")
        val result = evaluate(original, "0")

        assertNull(result.firedDirectionUp)
        assertEquals(original, result.alert)
    }

    @Test
    fun bothDirectionCanFireAfterRearmingOnOppositeHysteresisBand() {
        val firstShot = evaluate(alert(lastPrice = "100"), "101")
        assertEquals(true, firstShot.firedDirectionUp)

        val insideBand = evaluate(firstShot.alert, "99.95")
        assertNull(insideBand.firedDirectionUp)
        assertFalse(insideBand.alert.armed)

        val rearmed = evaluate(insideBand.alert, "99.89")
        assertNull(rearmed.firedDirectionUp)
        assertTrue(rearmed.alert.armed)
        assertEquals(true, rearmed.alert.firedUp)

        val nextShot = evaluate(rearmed.alert, "101")
        assertEquals(true, nextShot.firedDirectionUp)
    }

    @Test
    fun oneShotNeverRearmsAfterFiring() {
        val firstShot = evaluate(
            alert(repeatMode = RepeatMode.ONE_SHOT, lastPrice = "100"),
            "101",
        )
        val afterOppositeBand = evaluate(firstShot.alert, "99.89")

        assertNull(afterOppositeBand.firedDirectionUp)
        assertFalse(afterOppositeBand.alert.armed)
    }

    @Test
    fun repeatingAlertRearmsAtExactHysteresisBoundary() {
        val fired = evaluate(alert(lastPrice = "100"), "101")
        val atBoundary = evaluate(fired.alert, "99.90")

        assertTrue(atBoundary.alert.armed)
        assertNull(atBoundary.firedDirectionUp)
    }

    @Test
    fun downwardRepeatRearmsAtUpperHysteresisBoundary() {
        val firedDown = evaluate(alert(lastPrice = "100"), "99")
        val atBoundary = evaluate(firedDown.alert, "100.10")

        assertTrue(atBoundary.alert.armed)
        assertEquals(false, atBoundary.alert.firedUp)
    }

    private fun evaluate(alert: Alert, price: String): CrossingResult =
        CrossingDetector.evaluate(
            alert = alert,
            currentPriceBRL = BigDecimal(price),
            observedAt = Instant.parse("2026-01-01T00:00:00Z").plusSeconds(tickNumber++),
        )

    private fun alert(
        target: String = "100",
        direction: AlertDirection = AlertDirection.BOTH,
        repeatMode: RepeatMode = RepeatMode.REPEATING,
        armed: Boolean = true,
        firedUp: Boolean? = null,
        lastPrice: String? = null,
    ) = Alert(
        id = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        symbol = "BTC",
        targetPriceBRL = BigDecimal(target),
        direction = direction,
        repeatMode = repeatMode,
        armed = armed,
        firedUp = firedUp,
        lastPriceBRL = lastPrice?.let(::BigDecimal),
        enabled = true,
        createdAt = Instant.parse("2026-01-01T00:00:00Z"),
        updatedAt = Instant.parse("2026-01-01T00:00:00Z"),
        lastFiredAt = null,
        lastFiredTickAt = null,
    )
}
