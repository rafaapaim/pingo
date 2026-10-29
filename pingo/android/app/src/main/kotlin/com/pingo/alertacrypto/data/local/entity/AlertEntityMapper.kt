package com.pingo.alertacrypto.data.local.entity

import com.pingo.alertacrypto.domain.alert.Alert
import com.pingo.alertacrypto.domain.alert.AlertDirection
import com.pingo.alertacrypto.domain.alert.RepeatMode
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

fun AlertEntity.toDomain(): Alert = Alert(
    id = UUID.fromString(id),
    symbol = symbol,
    targetPriceBRL = BigDecimal(targetPriceBRL),
    direction = AlertDirection.valueOf(direction),
    repeatMode = RepeatMode.valueOf(repeatMode),
    armed = armed,
    firedUp = firedUp,
    lastPriceBRL = lastPriceBRL?.let(::BigDecimal),
    enabled = enabled,
    createdAt = Instant.ofEpochMilli(createdAtEpochMillis),
    updatedAt = Instant.ofEpochMilli(updatedAtEpochMillis),
    lastFiredAt = lastFiredAtEpochMillis?.let(Instant::ofEpochMilli),
    lastFiredTickAt = lastFiredTickAtEpochMillis?.let(Instant::ofEpochMilli),
)

fun Alert.toEntity(): AlertEntity = AlertEntity(
    id = id.toString(),
    symbol = symbol,
    targetPriceBRL = targetPriceBRL.toPlainString(),
    direction = direction.name,
    repeatMode = repeatMode.name,
    armed = armed,
    firedUp = firedUp,
    lastPriceBRL = lastPriceBRL?.toPlainString(),
    enabled = enabled,
    createdAtEpochMillis = createdAt.toEpochMilli(),
    updatedAtEpochMillis = updatedAt.toEpochMilli(),
    lastFiredAtEpochMillis = lastFiredAt?.toEpochMilli(),
    lastFiredTickAtEpochMillis = lastFiredTickAt?.toEpochMilli(),
)
