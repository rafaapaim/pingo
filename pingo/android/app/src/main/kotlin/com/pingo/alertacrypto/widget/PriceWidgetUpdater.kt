package com.pingo.alertacrypto.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PriceWidgetUpdater @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    suspend fun updateAllIfDue() {
        val now = System.currentTimeMillis()
        synchronized(lock) {
            if (now - lastUpdateAtMillis < MIN_UPDATE_INTERVAL_MILLIS) return
            lastUpdateAtMillis = now
        }
        try {
            PriceGlanceWidget().updateAll(context)
        } catch (error: Exception) {
            synchronized(lock) { lastUpdateAtMillis = 0L }
            Log.e(TAG, "Failed to request widget refresh", error)
            throw error
        }
    }

    companion object {
        private const val TAG = "PriceWidgetUpdater"
        private const val MIN_UPDATE_INTERVAL_MILLIS = 60_000L
        private val lock = Any()
        private var lastUpdateAtMillis = 0L
    }
}
