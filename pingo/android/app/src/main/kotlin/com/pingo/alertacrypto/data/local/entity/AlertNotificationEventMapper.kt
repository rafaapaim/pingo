package com.pingo.alertacrypto.data.local.entity

import com.pingo.alertacrypto.domain.alert.AlertNotificationEvent
import com.pingo.alertacrypto.domain.alert.NotificationDeliveryStatus
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

fun AlertNotificationEventEntity.toDomain(): AlertNotificationEvent = AlertNotificationEvent(
    eventId = UUID.fromString(eventId),
    alertId = UUID.fromString(alertId),
    symbol = symbol,
    directionUp = directionUp,
    targetPriceBRL = BigDecimal(targetPriceBRL),
    observedPriceBRL = BigDecimal(observedPriceBRL),
    tickObservedAt = Instant.ofEpochMilli(tickObservedAtEpochMillis),
    deliveryStatus = NotificationDeliveryStatus.valueOf(deliveryStatus),
    createdAt = Instant.ofEpochMilli(createdAtEpochMillis),
    updatedAt = Instant.ofEpochMilli(updatedAtEpochMillis),
)

fun AlertNotificationEvent.toEntity() = AlertNotificationEventEntity(
    eventId = eventId.toString(),
    alertId = alertId.toString(),
    symbol = symbol,
    directionUp = directionUp,
    targetPriceBRL = targetPriceBRL.toPlainString(),
    observedPriceBRL = observedPriceBRL.toPlainString(),
    tickObservedAtEpochMillis = tickObservedAt.toEpochMilli(),
    deliveryStatus = deliveryStatus.name,
    createdAtEpochMillis = createdAt.toEpochMilli(),
    updatedAtEpochMillis = updatedAt.toEpochMilli(),
)
