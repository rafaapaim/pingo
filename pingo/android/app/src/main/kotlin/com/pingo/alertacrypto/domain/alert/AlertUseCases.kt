package com.pingo.alertacrypto.domain.alert

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class AlertUseCases @Inject constructor(
    private val repository: AlertRepository,
) {
    fun observeAlerts(): Flow<List<Alert>> = repository.observeAll()

    suspend fun create(
        symbol: String,
        targetPriceBRL: BigDecimal,
        direction: AlertDirection,
        repeatMode: RepeatMode,
    ) {
        val now = Instant.now()
        repository.create(
            Alert(
                id = UUID.randomUUID(),
                symbol = symbol,
                targetPriceBRL = targetPriceBRL,
                direction = direction,
                repeatMode = repeatMode,
                armed = true,
                firedUp = null,
                lastPriceBRL = null,
                enabled = true,
                createdAt = now,
                updatedAt = now,
                lastFiredAt = null,
                lastFiredTickAt = null,
            ),
        )
    }

    suspend fun update(alert: Alert) = repository.update(alert)
    suspend fun setEnabled(id: UUID, enabled: Boolean) = repository.setEnabled(id, enabled)
    suspend fun delete(id: UUID) = repository.delete(id)
}
