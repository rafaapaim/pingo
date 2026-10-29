package com.pingo.alertacrypto.domain.monitoring

import java.time.Instant

enum class MonitoringStatus {
    STOPPED,
    CONTINUOUS,
    WORK_SCHEDULED,
    SYSTEM_INTERRUPTED,
    LIMITED,
}

enum class ConnectivityStatus {
    ONLINE,
    OFFLINE,
    UNKNOWN,
}

data class MonitoringState(
    val monitoringStatus: MonitoringStatus = MonitoringStatus.STOPPED,
    val connectivityStatus: ConnectivityStatus = ConnectivityStatus.UNKNOWN,
    val lastChangedAt: Instant = Instant.EPOCH,
    val reasonCode: String? = null,
    val continuousMonitoringEnabled: Boolean = false,
)

enum class MonitoringDataState {
    ONLINE,
    OFFLINE,
    STALE,
    MISSING,
}
