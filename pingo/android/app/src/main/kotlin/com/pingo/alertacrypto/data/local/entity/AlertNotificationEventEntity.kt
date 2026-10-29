package com.pingo.alertacrypto.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "alert_notification_events",
    indices = [Index(value = ["alertId", "tickObservedAtEpochMillis"], unique = true)],
)
data class AlertNotificationEventEntity(
    @PrimaryKey val eventId: String,
    val alertId: String,
    val symbol: String,
    val directionUp: Boolean,
    val targetPriceBRL: String,
    val observedPriceBRL: String,
    val tickObservedAtEpochMillis: Long,
    val deliveryStatus: String,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)
