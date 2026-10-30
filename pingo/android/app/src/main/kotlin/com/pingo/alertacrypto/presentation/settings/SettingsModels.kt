package com.pingo.alertacrypto.presentation.settings

import com.pingo.alertacrypto.R
import com.pingo.alertacrypto.domain.monitoring.ConnectivityStatus
import com.pingo.alertacrypto.domain.monitoring.MonitoringStatus
import com.pingo.alertacrypto.presentation.theme.StatusChipTone

data class MonitoringDisplay(
    val labelResource: Int,
    val messageResource: Int,
    val tone: StatusChipTone,
    val canReactivate: Boolean,
)

enum class NotificationDisplay {
    ENABLED,
    REQUESTABLE,
    SETTINGS_REQUIRED,
}

object SettingsStateMapper {
    fun monitoring(
        status: MonitoringStatus,
        connectivity: ConnectivityStatus,
        batterySaverActive: Boolean,
    ): MonitoringDisplay = when {
        connectivity == ConnectivityStatus.OFFLINE -> MonitoringDisplay(
            R.string.settings_status_offline,
            R.string.settings_monitor_offline,
            StatusChipTone.NEGATIVE,
            false,
        )
        batterySaverActive || status == MonitoringStatus.LIMITED -> MonitoringDisplay(
            R.string.settings_status_paused,
            R.string.settings_monitor_battery_paused,
            StatusChipTone.NEUTRAL,
            false,
        )
        status == MonitoringStatus.SYSTEM_INTERRUPTED -> MonitoringDisplay(
            R.string.settings_status_interrupted,
            R.string.settings_monitor_interrupted,
            StatusChipTone.WARNING,
            true,
        )
        status == MonitoringStatus.CONTINUOUS -> MonitoringDisplay(
            R.string.settings_status_active,
            R.string.settings_monitor_active,
            StatusChipTone.POSITIVE,
            false,
        )
        else ->
            MonitoringDisplay(
                R.string.settings_status_economic,
                R.string.settings_monitor_economic,
                StatusChipTone.NEUTRAL,
                false,
            )
    }

    fun notification(allowed: Boolean, canRequest: Boolean): NotificationDisplay = when {
        allowed -> NotificationDisplay.ENABLED
        canRequest -> NotificationDisplay.REQUESTABLE
        else -> NotificationDisplay.SETTINGS_REQUIRED
    }
}
