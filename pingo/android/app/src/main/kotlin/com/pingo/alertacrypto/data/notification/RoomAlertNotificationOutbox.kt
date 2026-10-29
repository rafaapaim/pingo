package com.pingo.alertacrypto.data.notification

import com.pingo.alertacrypto.data.local.dao.AlertNotificationEventDao
import com.pingo.alertacrypto.data.local.entity.toDomain
import com.pingo.alertacrypto.domain.alert.AlertNotificationEvent
import com.pingo.alertacrypto.domain.alert.NotificationDeliveryStatus
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

class RoomAlertNotificationOutbox @Inject constructor(
    private val dao: AlertNotificationEventDao,
) : AlertNotificationOutbox {
    override suspend fun pendingEvents(): List<AlertNotificationEvent> =
        dao.getPending().map { it.toDomain() }

    override suspend fun updateStatus(
        eventId: UUID,
        status: NotificationDeliveryStatus,
        updatedAt: Instant,
    ) {
        dao.updateDeliveryStatus(eventId.toString(), status.name, updatedAt.toEpochMilli())
    }
}
