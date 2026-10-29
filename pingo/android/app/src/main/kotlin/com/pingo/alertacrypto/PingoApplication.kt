package com.pingo.alertacrypto

import android.app.Application
import android.os.PowerManager
import com.pingo.alertacrypto.data.monitoring.MonitoringEntryPoint
import com.pingo.alertacrypto.domain.monitoring.MonitoringStatus
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltAndroidApp
class PingoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val monitoring = EntryPointAccessors.fromApplication(
            this,
            MonitoringEntryPoint::class.java,
        )
        monitoring.scheduler().schedulePeriodic()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            val previous = monitoring.stateStore().state.first()
            when (previous.monitoringStatus) {
                MonitoringStatus.CONTINUOUS -> monitoring.stateStore().update(
                    status = MonitoringStatus.SYSTEM_INTERRUPTED,
                    reasonCode = "process_restarted",
                )
                MonitoringStatus.STOPPED -> {
                    if (getSystemService(PowerManager::class.java).isPowerSaveMode) {
                        monitoring.stateStore().update(
                            status = MonitoringStatus.LIMITED,
                            reasonCode = "battery_saver",
                        )
                    } else {
                        monitoring.scheduler().markScheduled()
                    }
                }
                else -> Unit
            }
        }
    }
}
