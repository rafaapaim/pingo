package com.pingo.alertacrypto.domain.alert

import java.math.BigDecimal
import java.time.Instant

data class CrossingResult(
    val alert: Alert,
    val firedDirectionUp: Boolean?,
)

object CrossingDetector {
    private val HYSTERESIS = BigDecimal("0.001")
    private val ONE = BigDecimal.ONE

    fun evaluate(
        alert: Alert,
        currentPriceBRL: BigDecimal,
        observedAt: Instant,
    ): CrossingResult {
        if (!alert.enabled ||
            alert.targetPriceBRL <= BigDecimal.ZERO ||
            currentPriceBRL <= BigDecimal.ZERO ||
            alert.lastFiredTickAt == observedAt
        ) {
            return CrossingResult(alert, null)
        }

        if (!alert.armed) {
            val rearmed = alert.repeatMode == RepeatMode.REPEATING &&
                when (alert.firedUp) {
                    true -> currentPriceBRL <= alert.targetPriceBRL.multiply(ONE - HYSTERESIS)
                    false -> currentPriceBRL >= alert.targetPriceBRL.multiply(ONE + HYSTERESIS)
                    null -> false
                }
            return CrossingResult(
                alert = alert.copy(
                    armed = rearmed,
                    lastPriceBRL = currentPriceBRL,
                    updatedAt = observedAt,
                ),
                firedDirectionUp = null,
            )
        }

        val previousPrice = alert.lastPriceBRL
            ?: return CrossingResult(
                alert = alert.copy(lastPriceBRL = currentPriceBRL, updatedAt = observedAt),
                firedDirectionUp = null,
            )
        val crossedUp = previousPrice <= alert.targetPriceBRL &&
            currentPriceBRL > alert.targetPriceBRL
        val crossedDown = previousPrice >= alert.targetPriceBRL &&
            currentPriceBRL < alert.targetPriceBRL
        val acceptedDirection = when (alert.direction) {
            AlertDirection.UP -> crossedUp
            AlertDirection.DOWN -> crossedDown
            AlertDirection.BOTH -> crossedUp || crossedDown
        }
        val firedDirectionUp = when {
            acceptedDirection && crossedUp -> true
            acceptedDirection && crossedDown -> false
            else -> null
        }

        if (firedDirectionUp == null) {
            return CrossingResult(
                alert = alert.copy(lastPriceBRL = currentPriceBRL, updatedAt = observedAt),
                firedDirectionUp = null,
            )
        }

        return CrossingResult(
            alert = alert.copy(
                armed = false,
                firedUp = firedDirectionUp,
                lastPriceBRL = currentPriceBRL,
                updatedAt = observedAt,
                lastFiredAt = observedAt,
                lastFiredTickAt = observedAt,
            ),
            firedDirectionUp = firedDirectionUp,
        )
    }
}
