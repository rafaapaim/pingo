package com.pingo.alertacrypto.presentation.settings

import com.pingo.alertacrypto.R
import com.pingo.alertacrypto.domain.monitoring.ConnectivityStatus
import com.pingo.alertacrypto.domain.monitoring.MonitoringStatus
import com.pingo.alertacrypto.presentation.theme.StatusChipTone
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsStateMapperTest {
    @Test
    fun mapsMonitoringStatesToVisibleMessagesAndActions() {
        assertEquals(
            R.string.settings_monitor_offline,
            SettingsStateMapper.monitoring(
                MonitoringStatus.CONTINUOUS,
                ConnectivityStatus.OFFLINE,
                false,
            ).messageResource,
        )
        assertEquals(
            R.string.settings_monitor_interrupted,
            SettingsStateMapper.monitoring(
                MonitoringStatus.SYSTEM_INTERRUPTED,
                ConnectivityStatus.ONLINE,
                false,
            ).messageResource,
        )
        assertEquals(
            R.string.settings_monitor_battery_paused,
            SettingsStateMapper.monitoring(
                MonitoringStatus.CONTINUOUS,
                ConnectivityStatus.ONLINE,
                true,
            ).messageResource,
        )
        val interrupted = SettingsStateMapper.monitoring(
            MonitoringStatus.SYSTEM_INTERRUPTED,
            ConnectivityStatus.ONLINE,
            false,
        )
        assertEquals(StatusChipTone.WARNING, interrupted.tone)
        assertEquals(true, interrupted.canReactivate)
    }

    @Test
    fun mapsNotificationPermissionWithoutClaimingDeniedPermissionIsAllowed() {
        assertEquals(
            NotificationDisplay.ENABLED,
            SettingsStateMapper.notification(allowed = true, canRequest = false),
        )
        assertEquals(
            NotificationDisplay.REQUESTABLE,
            SettingsStateMapper.notification(allowed = false, canRequest = true),
        )
        assertEquals(
            NotificationDisplay.SETTINGS_REQUIRED,
            SettingsStateMapper.notification(allowed = false, canRequest = false),
        )
    }
}
