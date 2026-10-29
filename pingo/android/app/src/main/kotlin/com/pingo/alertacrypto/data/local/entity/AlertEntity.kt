package com.pingo.alertacrypto.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "alerts",
    indices = [Index(value = ["symbol", "enabled"])],
)
data class AlertEntity(
    @PrimaryKey val id: String,
    val symbol: String,
    val targetPriceBRL: String,
    val direction: String,
    val repeatMode: String,
    val armed: Boolean,
    val firedUp: Boolean?,
    val lastPriceBRL: String?,
    val enabled: Boolean,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
    val lastFiredAtEpochMillis: Long?,
    val lastFiredTickAtEpochMillis: Long?,
)
