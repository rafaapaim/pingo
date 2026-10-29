package com.pingo.alertacrypto.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "latest_prices")
data class LatestPriceEntity(
    @PrimaryKey val symbol: String,
    val priceBRL: String,
    val change24hPercent: String?,
    val observedAtEpochMillis: Long,
    val receivedAtEpochMillis: Long,
    val sourceId: String,
)
