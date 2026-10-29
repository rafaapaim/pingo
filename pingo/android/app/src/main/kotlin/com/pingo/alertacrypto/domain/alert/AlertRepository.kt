package com.pingo.alertacrypto.domain.alert

import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface AlertRepository {
    fun observeAll(): Flow<List<Alert>>
    suspend fun create(alert: Alert)
    suspend fun update(alert: Alert)
    suspend fun setEnabled(id: UUID, enabled: Boolean)
    suspend fun delete(id: UUID)
}
