package com.pingo.alertacrypto.data.alert

import androidx.room.withTransaction
import com.pingo.alertacrypto.data.local.AppDatabase
import com.pingo.alertacrypto.data.local.entity.AlertNotificationEventEntity
import com.pingo.alertacrypto.data.local.entity.toDomain
import com.pingo.alertacrypto.data.local.entity.toEntity
import com.pingo.alertacrypto.data.local.entity.LatestPriceEntity
import com.pingo.alertacrypto.domain.alert.AlertNotificationEvent
import com.pingo.alertacrypto.domain.alert.CrossingDetector
import com.pingo.alertacrypto.domain.alert.NotificationDeliveryStatus
import com.pingo.alertacrypto.domain.alert.ProcessPriceTick
import com.pingo.alertacrypto.domain.alert.TickProcessingResult
import com.pingo.alertacrypto.domain.price.Asset
import com.pingo.alertacrypto.domain.price.PriceTick
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Singleton
class PriceTickProcessor @Inject constructor(
    private val database: AppDatabase,
) : ProcessPriceTick {
    private val symbolLocks = mutableMapOf<String, Mutex>()

    override suspend fun process(tick: PriceTick, sourceId: String): TickProcessingResult {
        require(Asset.fromSymbol(tick.symbol)?.symbol == tick.symbol) {
            "Unsupported or non-canonical asset symbol: ${tick.symbol}"
        }
        require(tick.priceBRL > java.math.BigDecimal.ZERO) { "Price must be positive" }
        val lock = synchronized(symbolLocks) {
            symbolLocks.getOrPut(tick.symbol) { Mutex() }
        }
        return lock.withLock {
            database.withTransaction {
                val latestDao = database.latestPriceDao()
                val previousTickAt = latestDao.getBySymbol(tick.symbol)?.observedAtEpochMillis
                val observedAtMillis = tick.observedAt.toEpochMilli()
                if (previousTickAt != null && previousTickAt >= observedAtMillis) {
                    return@withTransaction TickProcessingResult(accepted = false, eventsCreated = 0)
                }

                var eventsCreated = 0
                val alertDao = database.alertDao()
                alertDao.getEnabledForSymbol(tick.symbol).forEach { entity ->
                    val alert = entity.toDomain()
                    val result = CrossingDetector.evaluate(alert, tick.priceBRL, tick.observedAt)
                    if (result.alert != alert) alertDao.update(result.alert.toEntity())
                    val directionUp = result.firedDirectionUp ?: return@forEach
                    val event = AlertNotificationEvent(
                        eventId = stableEventId(alert.id, observedAtMillis),
                        alertId = alert.id,
                        symbol = alert.symbol,
                        directionUp = directionUp,
                        targetPriceBRL = alert.targetPriceBRL,
                        observedPriceBRL = tick.priceBRL,
                        tickObservedAt = tick.observedAt,
                        deliveryStatus = NotificationDeliveryStatus.PENDING,
                        createdAt = Instant.now(),
                        updatedAt = Instant.now(),
                    )
                    val inserted = database.alertNotificationEventDao().insertIfAbsent(event.toEntity())
                    if (inserted != -1L) eventsCreated += 1
                }
                latestDao.upsert(
                    LatestPriceEntity(
                        symbol = tick.symbol,
                        priceBRL = tick.priceBRL.toPlainString(),
                        change24hPercent = tick.change24hPercent?.toPlainString(),
                        observedAtEpochMillis = observedAtMillis,
                        receivedAtEpochMillis = System.currentTimeMillis(),
                        sourceId = sourceId,
                    ),
                )
                TickProcessingResult(accepted = true, eventsCreated = eventsCreated)
            }
        }
    }

    private fun stableEventId(alertId: UUID, tickObservedAtMillis: Long): UUID =
        UUID.nameUUIDFromBytes("$alertId:$tickObservedAtMillis".toByteArray(StandardCharsets.UTF_8))
}

private fun AlertNotificationEvent.toEntity() = AlertNotificationEventEntity(
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
