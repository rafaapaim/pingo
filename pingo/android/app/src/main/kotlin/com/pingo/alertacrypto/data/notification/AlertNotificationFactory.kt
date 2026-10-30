package com.pingo.alertacrypto.data.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.pingo.alertacrypto.R
import com.pingo.alertacrypto.domain.alert.AlertNotificationEvent
import com.pingo.alertacrypto.presentation.navigation.PriceDetailDestination
import java.util.UUID

class AlertNotificationFactory(
    private val context: Context,
) : AlertEventPublisher {
    init {
        createChannels()
    }

    override fun publish(event: AlertNotificationEvent, notificationId: Int) {
        if (!notificationsAllowed(context)) {
            throw SecurityException("Notification permission is not available")
        }
        val pendingIntent = PriceDetailDestination.pendingIntent(context, event.symbol)
        val content = AlertNotificationTextFormatter.format(
            event = event,
            upTemplate = context.getString(R.string.alert_notification_up),
            downTemplate = context.getString(R.string.alert_notification_down),
        )
        val notification = NotificationCompat.Builder(context, ALERT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_pingo)
            .setContentTitle(context.getString(R.string.alert_notification_title))
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        notify(notificationId, notification)
    }

    fun monitoringNotification(): Notification =
        NotificationCompat.Builder(context, MONITORING_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_pingo)
            .setContentTitle(context.getString(R.string.monitoring_notification_title))
            .setContentText(context.getString(R.string.monitoring_notification_active))
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

    fun postMonitoringInterrupted() {
        if (!notificationsAllowed(context)) return
        val intent = Intent(context, com.pingo.alertacrypto.MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(
            context,
            MONITORING_NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, ALERT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_pingo)
            .setContentTitle(context.getString(R.string.monitoring_interrupted_title))
            .setContentText(context.getString(R.string.monitoring_interrupted_message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        notify(MONITORING_NOTIFICATION_ID, notification)
    }

    private fun createChannels() {
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(
                ALERT_CHANNEL_ID,
                context.getString(R.string.alert_channel_name),
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = context.getString(R.string.alert_channel_description)
            },
        )
        manager.createNotificationChannel(
            NotificationChannel(
                MONITORING_CHANNEL_ID,
                context.getString(R.string.monitoring_channel_name),
                NotificationManager.IMPORTANCE_LOW,
            ),
        )
    }

    private fun notify(notificationId: Int, notification: Notification) {
        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (error: SecurityException) {
            throw error
        }
    }

    companion object {
        const val ALERT_CHANNEL_ID = "price_alerts"
        const val MONITORING_CHANNEL_ID = "price_monitoring"
        const val MONITORING_NOTIFICATION_ID = 0x50494e

        fun notificationsAllowed(context: Context): Boolean =
            NotificationManagerCompat.from(context).areNotificationsEnabled() &&
                (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS,
                    ) == PackageManager.PERMISSION_GRANTED)

        fun notificationIdFor(eventId: UUID): Int = AlertNotificationDispatcher.notificationId(eventId)
    }
}
