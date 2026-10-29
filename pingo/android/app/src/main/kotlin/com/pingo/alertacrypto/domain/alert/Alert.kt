package com.pingo.alertacrypto.domain.alert

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class Alert(
    val id: UUID,
    val symbol: String,
    val targetPriceBRL: BigDecimal,
    val direction: AlertDirection,
    val repeatMode: RepeatMode,
    val armed: Boolean,
    val firedUp: Boolean?,
    val lastPriceBRL: BigDecimal?,
    val enabled: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
    val lastFiredAt: Instant?,
    val lastFiredTickAt: Instant?,
)

enum class AlertDirection {
    UP,
    DOWN,
    BOTH,
}

enum class RepeatMode {
    ONE_SHOT,
    REPEATING,
}

enum class NotificationDeliveryStatus {
    PENDING,
    POSTED,
    BLOCKED_PERMISSION,
    FAILED,
}
