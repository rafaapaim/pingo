package com.pingo.alertacrypto.data.monitoring

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pingo.alertacrypto.domain.monitoring.ConnectivityStatus
import com.pingo.alertacrypto.domain.monitoring.MonitoringState
import com.pingo.alertacrypto.domain.monitoring.MonitoringStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.monitoringDataStore by preferencesDataStore(name = "monitoring_state")

class MonitoringStateStore @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val dataStore = context.monitoringDataStore

    val state: Flow<MonitoringState> = dataStore.data.map { preferences ->
        MonitoringState(
            monitoringStatus = preferences[STATUS]?.let(::enumValueOrNull)
                ?: MonitoringStatus.STOPPED,
            connectivityStatus = preferences[CONNECTIVITY]?.let(::enumValueOrNull)
                ?: ConnectivityStatus.UNKNOWN,
            lastChangedAt = Instant.ofEpochMilli(preferences[LAST_CHANGED] ?: 0L),
            reasonCode = preferences[REASON],
            continuousMonitoringEnabled = preferences[CONTINUOUS_ENABLED] ?: false,
        )
    }

    suspend fun setContinuousEnabled(enabled: Boolean) {
        dataStore.edit { it[CONTINUOUS_ENABLED] = enabled }
    }

    suspend fun update(
        status: MonitoringStatus? = null,
        connectivity: ConnectivityStatus? = null,
        reasonCode: String? = null,
        clearReason: Boolean = false,
    ) {
        dataStore.edit { preferences ->
            if (status != null) preferences[STATUS] = status.name
            if (connectivity != null) preferences[CONNECTIVITY] = connectivity.name
            if (clearReason) preferences.remove(REASON)
            else if (reasonCode != null) preferences[REASON] = reasonCode
            preferences[LAST_CHANGED] = System.currentTimeMillis()
        }
    }

    private inline fun <reified T : Enum<T>> enumValueOrNull(value: String): T? =
        enumValues<T>().firstOrNull { it.name == value }

    private companion object {
        val STATUS = stringPreferencesKey("monitoring_status")
        val CONNECTIVITY = stringPreferencesKey("connectivity_status")
        val LAST_CHANGED = androidx.datastore.preferences.core.longPreferencesKey("last_changed")
        val REASON = stringPreferencesKey("reason_code")
        val CONTINUOUS_ENABLED = booleanPreferencesKey("continuous_enabled")
    }
}
