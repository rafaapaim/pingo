package com.pingo.alertacrypto.data.notification

import com.pingo.alertacrypto.domain.alert.AlertNotificationEvent
import com.pingo.alertacrypto.domain.alert.NotificationDeliveryStatus
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface AlertNotificationOutbox {
    suspend fun pendingEvents(): List<AlertNotificationEvent>
    suspend fun updateStatus(
        eventId: UUID,
        status: NotificationDeliveryStatus,
        updatedAt: Instant,
    )
}

fun interface AlertEventPublisher {
    fun publish(event: AlertNotificationEvent, notificationId: Int)
}

data class DispatchFailure(val eventId: UUID, val cause: Exception)

data class DispatchResult(
    val posted: Int,
    val blockedByPermission: Int,
    val failures: List<DispatchFailure>,
)

class AlertNotificationDispatcher(
    private val outbox: AlertNotificationOutbox,
    private val publisher: AlertEventPublisher,
    private val clock: () -> Instant = Instant::now,
) {
    private val dispatchMutex = Mutex()

    suspend fun dispatchPending(notificationsAllowed: Boolean): DispatchResult {
        return dispatchMutex.withLock {
            var posted = 0
            var blocked = 0
            val failures = mutableListOf<DispatchFailure>()
            outbox.pendingEvents().forEach { event ->
                if (!notificationsAllowed) {
                    outbox.updateStatus(
                        event.eventId,
                        NotificationDeliveryStatus.BLOCKED_PERMISSION,
                        clock(),
                    )
                    blocked += 1
                    return@forEach
                }
                try {
                    publisher.publish(event, notificationId(event.eventId))
                    outbox.updateStatus(event.eventId, NotificationDeliveryStatus.POSTED, clock())
                    posted += 1
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    outbox.updateStatus(event.eventId, NotificationDeliveryStatus.FAILED, clock())
                    failures += DispatchFailure(event.eventId, error)
                }
            }
            DispatchResult(posted, blocked, failures)
        }
    }

    companion object {
        fun notificationId(eventId: UUID): Int {
            val digest = MessageDigest.getInstance("SHA-256")
                .digest(eventId.toString().toByteArray(StandardCharsets.UTF_8))
            return ((digest[0].toInt() and 0x7f) shl 24) or
                ((digest[1].toInt() and 0xff) shl 16) or
                ((digest[2].toInt() and 0xff) shl 8) or
                (digest[3].toInt() and 0xff)
        }
    }
}
