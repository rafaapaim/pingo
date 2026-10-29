package com.pingo.alertacrypto.data.monitoring

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.pingo.alertacrypto.domain.monitoring.ConnectivityStatus
import com.pingo.alertacrypto.domain.monitoring.MonitoringStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MonitoringScheduler @Inject constructor(
    @ApplicationContext context: Context,
    private val stateStore: MonitoringStateStore,
) {
    private val workManager = WorkManager.getInstance(context)

    fun schedulePeriodic() {
        val request = PeriodicWorkRequestBuilder<PriceCheckWorker>(
            PERIOD_MINUTES,
            TimeUnit.MINUTES,
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build(),
            )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.MINUTES)
            .build()
        workManager.enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }

    suspend fun markScheduled(reasonCode: String? = null) {
        stateStore.update(
            status = MonitoringStatus.WORK_SCHEDULED,
            reasonCode = reasonCode,
            clearReason = reasonCode == null,
        )
    }

    companion object {
        const val PERIOD_MINUTES = 15L
        const val UNIQUE_WORK_NAME = "crypto-price-check-periodic"
    }
}
