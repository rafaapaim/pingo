package com.pingo.alertacrypto.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pingo.alertacrypto.data.local.entity.AlertNotificationEventEntity

@Dao
interface AlertNotificationEventDao {
    @Query("SELECT * FROM alert_notification_events WHERE deliveryStatus IN ('PENDING', 'FAILED', 'BLOCKED_PERMISSION') ORDER BY createdAtEpochMillis")
    suspend fun getPending(): List<AlertNotificationEventEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(event: AlertNotificationEventEntity): Long

    @Query("UPDATE alert_notification_events SET deliveryStatus = :status, updatedAtEpochMillis = :updatedAt WHERE eventId = :eventId")
    suspend fun updateDeliveryStatus(eventId: String, status: String, updatedAt: Long)
}
