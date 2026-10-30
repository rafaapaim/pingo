package com.pingo.alertacrypto.data.monitoring

import android.content.Context
import android.os.PowerManager
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.hilt.android.EntryPointAccessors
import com.pingo.alertacrypto.data.notification.AlertNotificationFactory
import com.pingo.alertacrypto.domain.monitoring.ConnectivityStatus
import com.pingo.alertacrypto.domain.monitoring.MonitoringStatus
import com.pingo.alertacrypto.domain.price.Asset
import java.io.IOException
import kotlinx.coroutines.CancellationException

class PriceCheckWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val dependencies = EntryPointAccessors.fromApplication(
            applicationContext,
            MonitoringEntryPoint::class.java,
        )
        val stateStore = dependencies.stateStore()
        val powerManager = applicationContext.getSystemService(PowerManager::class.java)
        if (powerManager.isPowerSaveMode) {
            stateStore.update(
                status = MonitoringStatus.LIMITED,
                reasonCode = "battery_saver",
            )
            return Result.success()
        }

        return try {
            val prices = dependencies.coinGeckoPriceSource()
                .fetchLatest(Asset.supported.map(Asset::symbol).toSet())
            prices.forEach { tick ->
                dependencies.processor().process(tick, "coingecko_rest")
            }
            dependencies.priceWidgetUpdater().updateAllIfDue()
            val allowed = AlertNotificationFactory.notificationsAllowed(applicationContext)
            val dispatchResult = dependencies.dispatcher().dispatchPending(allowed)
            stateStore.update(
                status = MonitoringStatus.WORK_SCHEDULED,
                connectivity = ConnectivityStatus.ONLINE,
                clearReason = true,
            )
            dispatchResult.failures.forEach { failure ->
                Log.e(TAG, "Failed to publish alert ${failure.eventId}", failure.cause)
            }
            if (dispatchResult.failures.isNotEmpty()) Result.retry() else Result.success()
        } catch (error: CancellationException) {
            throw error
        } catch (error: IOException) {
            Log.w(TAG, "Price check failed; WorkManager will retry", error)
            stateStore.update(
                status = MonitoringStatus.WORK_SCHEDULED,
                connectivity = ConnectivityStatus.OFFLINE,
                reasonCode = "price_source_unavailable",
            )
            Result.retry()
        }

    }

    companion object {
        private const val TAG = "PriceCheckWorker"
    }
}
