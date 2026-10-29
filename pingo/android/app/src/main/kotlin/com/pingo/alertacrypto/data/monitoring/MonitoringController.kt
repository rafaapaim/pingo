package com.pingo.alertacrypto.data.monitoring

import android.content.Context
import android.content.Intent
import android.os.PowerManager
import androidx.core.content.ContextCompat
import com.pingo.alertacrypto.domain.monitoring.MonitoringStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MonitoringController @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scheduler: MonitoringScheduler,
    private val stateStore: MonitoringStateStore,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun setContinuousEnabled(enabled: Boolean) {
        scope.launch {
            stateStore.setContinuousEnabled(enabled)
            if (!enabled) {
                context.stopService(Intent(context, PriceMonitoringService::class.java))
                scheduler.schedulePeriodic()
                scheduler.markScheduled()
                return@launch
            }
            val powerManager = context.getSystemService(PowerManager::class.java)
            if (powerManager.isPowerSaveMode) {
                scheduler.schedulePeriodic()
                stateStore.update(
                    status = MonitoringStatus.LIMITED,
                    reasonCode = "battery_saver",
                )
                return@launch
            }
            ContextCompat.startForegroundService(
                context,
                Intent(context, PriceMonitoringService::class.java)
                    .setAction(PriceMonitoringService.ACTION_START),
            )
        }
    }
}
