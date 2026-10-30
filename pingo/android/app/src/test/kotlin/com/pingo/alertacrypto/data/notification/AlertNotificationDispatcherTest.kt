package com.pingo.alertacrypto.data.notification

import com.pingo.alertacrypto.domain.alert.AlertNotificationEvent
import com.pingo.alertacrypto.domain.alert.NotificationDeliveryStatus
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.async
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AlertNotificationDispatcherTest {
    @Test
    fun formatsPortugueseBrazilNotificationWithBrazilianCurrency() {
        val text = AlertNotificationTextFormatter.format(
            event = event(),
            upTemplate = "%1\$s passou de %2\$s (agora %3\$s) ↑",
            downTemplate = "%1\$s caiu abaixo de %2\$s (agora %3\$s) ↓",
        )

        assertEquals("BTC passou de R$ 1.234,56 (agora R$ 1.250,00) ↑", text)
    }

    @Test
    fun formatsDownwardNotificationInPortugueseBrazil() {
        val text = AlertNotificationTextFormatter.format(
            event = event().copy(
                directionUp = false,
                targetPriceBRL = BigDecimal("1234.56"),
                observedPriceBRL = BigDecimal("1200.00"),
            ),
            upTemplate = "%1\$s passou de %2\$s (agora %3\$s) ↑",
            downTemplate = "%1\$s caiu abaixo de %2\$s (agora %3\$s) ↓",
        )

        assertEquals("BTC caiu abaixo de R$ 1.234,56 (agora R$ 1.200,00) ↓", text)
    }

    @Test
    fun publishesPendingEventOnceAndMarksItPosted() = runBlocking {
        val event = event()
        val store = FakeOutbox(listOf(event))
        val publisher = FakePublisher()
        val dispatcher = AlertNotificationDispatcher(store, publisher)

        assertEquals(1, dispatcher.dispatchPending(notificationsAllowed = true).posted)
        assertEquals(0, dispatcher.dispatchPending(notificationsAllowed = true).posted)
        assertEquals(1, publisher.published.size)
        assertEquals(NotificationDeliveryStatus.POSTED, store.events.single().deliveryStatus)
        assertEquals(
            AlertNotificationDispatcher.notificationId(event.eventId),
            publisher.ids.single(),
        )
    }

    @Test
    fun deniedPermissionMarksBlockedWithoutPublishing() = runBlocking {
        val store = FakeOutbox(listOf(event()))
        val publisher = FakePublisher()

        val dispatched = AlertNotificationDispatcher(store, publisher)
            .dispatchPending(notificationsAllowed = false)

        assertEquals(0, dispatched.posted)
        assertEquals(1, dispatched.blockedByPermission)
        assertTrue(publisher.published.isEmpty())
        assertEquals(
            NotificationDeliveryStatus.BLOCKED_PERMISSION,
            store.events.single().deliveryStatus,
        )
    }

    @Test
    fun retryAfterPublisherFailureReusesTheSameNotificationId() = runBlocking {
        val store = FakeOutbox(listOf(event()))
        val publisher = FakePublisher(failFirstAttempt = true)
        val dispatcher = AlertNotificationDispatcher(store, publisher)

        assertEquals(0, dispatcher.dispatchPending(notificationsAllowed = true).posted)
        assertEquals(NotificationDeliveryStatus.FAILED, store.events.single().deliveryStatus)
        assertEquals(1, dispatcher.dispatchPending(notificationsAllowed = true).posted)

        assertEquals(2, publisher.ids.size)
        assertEquals(publisher.ids[0], publisher.ids[1])
        assertEquals(NotificationDeliveryStatus.POSTED, store.events.single().deliveryStatus)
    }

    @Test
    fun concurrentDispatchersDoNotPublishTheSamePendingEventTwice() = runBlocking {
        val store = FakeOutbox(listOf(event()))
        val publisher = FakePublisher()
        val dispatcher = AlertNotificationDispatcher(store, publisher)

        val first = async { dispatcher.dispatchPending(notificationsAllowed = true) }
        val second = async { dispatcher.dispatchPending(notificationsAllowed = true) }

        assertEquals(1, first.await().posted + second.await().posted)
        assertEquals(1, publisher.published.size)
    }

    @Test
    fun blockedPermissionEventCanBeDeliveredAfterPermissionIsGranted() = runBlocking {
        val store = FakeOutbox(listOf(event()))
        val publisher = FakePublisher()
        val dispatcher = AlertNotificationDispatcher(store, publisher)

        dispatcher.dispatchPending(notificationsAllowed = false)
        val result = dispatcher.dispatchPending(notificationsAllowed = true)

        assertEquals(1, result.posted)
        assertEquals(NotificationDeliveryStatus.POSTED, store.events.single().deliveryStatus)
    }

    private fun event() = AlertNotificationEvent(
        eventId = UUID.fromString("00000000-0000-0000-0000-000000000101"),
        alertId = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        symbol = "BTC",
        directionUp = true,
        targetPriceBRL = BigDecimal("1234.56"),
        observedPriceBRL = BigDecimal("1250.00"),
        tickObservedAt = Instant.parse("2026-01-01T00:00:00Z"),
        deliveryStatus = NotificationDeliveryStatus.PENDING,
        createdAt = Instant.parse("2026-01-01T00:00:00Z"),
        updatedAt = Instant.parse("2026-01-01T00:00:00Z"),
    )

    private class FakeOutbox(initialEvents: List<AlertNotificationEvent>) : AlertNotificationOutbox {
        val events = initialEvents.toMutableList()

        override suspend fun pendingEvents() =
            events.filter {
                it.deliveryStatus == NotificationDeliveryStatus.PENDING ||
                    it.deliveryStatus == NotificationDeliveryStatus.FAILED ||
                    it.deliveryStatus == NotificationDeliveryStatus.BLOCKED_PERMISSION
            }

        override suspend fun updateStatus(
            eventId: UUID,
            status: NotificationDeliveryStatus,
            updatedAt: Instant,
        ) {
            val index = events.indexOfFirst { it.eventId == eventId }
            events[index] = events[index].copy(deliveryStatus = status, updatedAt = updatedAt)
        }
    }

    private class FakePublisher(
        private val failFirstAttempt: Boolean = false,
    ) : AlertEventPublisher {
        val published = mutableListOf<AlertNotificationEvent>()
        val ids = mutableListOf<Int>()

        override fun publish(event: AlertNotificationEvent, notificationId: Int) {
            ids += notificationId
            if (failFirstAttempt && ids.size == 1) error("test notification failure")
            published += event
        }
    }
}
