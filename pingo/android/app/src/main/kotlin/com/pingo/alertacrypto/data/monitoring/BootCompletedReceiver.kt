package com.pingo.alertacrypto.data.monitoring

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.EntryPointAccessors

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            MonitoringEntryPoint::class.java,
        )
        entryPoint.scheduler().schedulePeriodic()
    }
}
