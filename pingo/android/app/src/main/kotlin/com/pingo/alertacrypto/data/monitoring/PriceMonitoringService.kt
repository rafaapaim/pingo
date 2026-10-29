package com.pingo.alertacrypto.data.monitoring

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.app.Service
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.pingo.alertacrypto.data.notification.AlertNotificationFactory
import com.pingo.alertacrypto.domain.monitoring.ConnectivityStatus
import com.pingo.alertacrypto.domain.monitoring.MonitoringStatus
import dagger.hilt.android.EntryPointAccessors
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.min

class PriceMonitoringService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var monitoringJob: Job? = null
    private lateinit var dependencies: MonitoringEntryPoint
    private var powerSaveReceiverRegistered = false

    private val powerSaveReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (getSystemService(PowerManager::class.java).isPowerSaveMode) {
                pauseForBatterySaver()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        dependencies = EntryPointAccessors.fromApplication(
            applicationContext,
            MonitoringEntryPoint::class.java,
        )
        ContextCompat.registerReceiver(
            this,
            powerSaveReceiver,
            IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED),
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
        powerSaveReceiverRegistered = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                dependencies.scheduler().schedulePeriodic()
                serviceScope.launch {
                    dependencies.stateStore().setContinuousEnabled(false)
                    dependencies.scheduler().markScheduled()
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf(startId)
                }
                return START_NOT_STICKY
            }
            ACTION_START -> Unit
            null -> return START_NOT_STICKY
            else -> return START_NOT_STICKY
        }

        if (getSystemService(PowerManager::class.java).isPowerSaveMode) {
            pauseForBatterySaver()
            return START_NOT_STICKY
        }

        val notification = AlertNotificationFactory(this).monitoringNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                MONITORING_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC,
            )
        } else {
            startForeground(MONITORING_NOTIFICATION_ID, notification)
        }
        dependencies.scheduler().schedulePeriodic()
        serviceScope.launch {
            dependencies.stateStore().update(
                status = MonitoringStatus.CONTINUOUS,
                connectivity = ConnectivityStatus.UNKNOWN,
                clearReason = true,
            )
        }
        if (monitoringJob?.isActive != true) monitoringJob = serviceScope.launch { observePrices() }
        return START_NOT_STICKY
    }

    private suspend fun observePrices() {
        val symbols = com.pingo.alertacrypto.domain.price.Asset.supported
            .map { it.symbol }
            .toSet()
        var retryDelayMillis = INITIAL_RETRY_DELAY_MILLIS
        var connectivity = ConnectivityStatus.UNKNOWN
        while (serviceScope.isActive) {
            try {
                dependencies.binancePriceSource().observeTicks(symbols).collect { tick ->
                    dependencies.processor().process(tick, "binance_ws")
                    if (connectivity != ConnectivityStatus.ONLINE) {
                        dependencies.stateStore().update(
                            status = MonitoringStatus.CONTINUOUS,
                            connectivity = ConnectivityStatus.ONLINE,
                            clearReason = true,
                        )
                        connectivity = ConnectivityStatus.ONLINE
                    }
                    val dispatch = dependencies.dispatcher().dispatchPending(
                        AlertNotificationFactory.notificationsAllowed(this@PriceMonitoringService),
                    )
                    dispatch.failures.forEach { failure ->
                        Log.e(TAG, "Failed to publish alert ${failure.eventId}", failure.cause)
                    }
                    retryDelayMillis = INITIAL_RETRY_DELAY_MILLIS
                }
                throw IOException("Price stream ended")
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.w(TAG, "WebSocket price stream failed; using REST fallback", error)
                if (connectivity != ConnectivityStatus.OFFLINE) {
                    dependencies.stateStore().update(
                        status = MonitoringStatus.CONTINUOUS,
                        connectivity = ConnectivityStatus.OFFLINE,
                        reasonCode = "price_source_unavailable",
                    )
                    connectivity = ConnectivityStatus.OFFLINE
                }
                try {
                    dependencies.coinGeckoPriceSource()
                        .fetchLatest(symbols)
                        .forEach { tick ->
                            dependencies.processor().process(tick, "coingecko_rest")
                        }
                    dependencies.stateStore().update(
                        status = MonitoringStatus.CONTINUOUS,
                        connectivity = ConnectivityStatus.ONLINE,
                        clearReason = true,
                    )
                    connectivity = ConnectivityStatus.ONLINE
                    val dispatch = dependencies.dispatcher().dispatchPending(
                        AlertNotificationFactory.notificationsAllowed(this@PriceMonitoringService),
                    )
                    dispatch.failures.forEach { failure ->
                        Log.e(TAG, "Failed to publish alert ${failure.eventId}", failure.cause)
                    }
                } catch (fallbackError: CancellationException) {
                    throw fallbackError
                } catch (fallbackError: IOException) {
                    Log.w(TAG, "REST price fallback failed", fallbackError)
                }
                delay(retryDelayMillis)
                retryDelayMillis = min(retryDelayMillis * 2, MAX_RETRY_DELAY_MILLIS)
            }
        }
    }

    private fun pauseForBatterySaver() {
        dependencies.scheduler().schedulePeriodic()
        monitoringJob?.cancel()
        serviceScope.launch {
            dependencies.stateStore().update(
                status = MonitoringStatus.LIMITED,
                reasonCode = "battery_saver",
            )
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    override fun onTimeout(startId: Int, fgsType: Int) {
        dependencies.scheduler().schedulePeriodic()
        monitoringJob?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        serviceScope.launch {
            try {
                dependencies.stateStore().update(
                    status = MonitoringStatus.SYSTEM_INTERRUPTED,
                    reasonCode = "data_sync_timeout",
                )
                if (AlertNotificationFactory.notificationsAllowed(this@PriceMonitoringService)) {
                    AlertNotificationFactory(this@PriceMonitoringService).postMonitoringInterrupted()
                }
            } catch (error: SecurityException) {
                Log.w(TAG, "Could not post foreground-service timeout notification", error)
            } finally {
                stopSelf(startId)
            }
        }
    }

    override fun onDestroy() {
        if (powerSaveReceiverRegistered) unregisterReceiver(powerSaveReceiver)
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val TAG = "PriceMonitoringService"
        const val ACTION_START = "com.pingo.alertacrypto.action.START_MONITORING"
        const val ACTION_STOP = "com.pingo.alertacrypto.action.STOP_MONITORING"
        const val MONITORING_NOTIFICATION_ID = 0x50494f
        private const val INITIAL_RETRY_DELAY_MILLIS = 1_000L
        private const val MAX_RETRY_DELAY_MILLIS = 60_000L
    }
}
