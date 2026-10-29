package com.pingo.alertacrypto.presentation.monitoring

import android.os.Build
import android.os.PowerManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pingo.alertacrypto.R
import com.pingo.alertacrypto.data.notification.AlertNotificationFactory
import com.pingo.alertacrypto.domain.monitoring.ConnectivityStatus
import com.pingo.alertacrypto.domain.monitoring.MonitoringStatus
import com.pingo.alertacrypto.domain.monitoring.QuoteFreshness
import com.pingo.alertacrypto.presentation.prices.PriceConnectionState
import com.pingo.alertacrypto.presentation.prices.PriceListUiState
import com.pingo.alertacrypto.presentation.format.PriceFormatter

@Composable
fun MonitoringStatusBanner(
    uiState: PriceListUiState,
    onContinuousMonitoringChanged: (Boolean) -> Unit,
    onRequestNotificationPermission: () -> Unit,
) {
    val context = LocalContext.current
    val permissionAllowed = AlertNotificationFactory.notificationsAllowed(context)
    val batterySaverActive = context.getSystemService(PowerManager::class.java).isPowerSaveMode
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        when {
            uiState.connectionState == PriceConnectionState.OFFLINE ||
                uiState.monitoringState.connectivityStatus == ConnectivityStatus.OFFLINE ->
                Text(
                    stringResource(R.string.monitoring_data_offline),
                    color = MaterialTheme.colorScheme.error,
                )
            uiState.assets.any { it.freshness == QuoteFreshness.STALE } -> {
                val stale = uiState.assets.first { it.freshness == QuoteFreshness.STALE }
                Text(
                    stringResource(
                        R.string.monitoring_data_stale,
                        stale.tick?.let { PriceFormatter.time(it.observedAt) }
                            ?: stringResource(R.string.price_not_available),
                    ),
                    color = MaterialTheme.colorScheme.error,
                )
            }
            uiState.assets.all { it.freshness == QuoteFreshness.MISSING } ->
                Text(stringResource(R.string.monitoring_data_missing))
            uiState.connectionState == PriceConnectionState.ONLINE ||
                uiState.monitoringState.connectivityStatus == ConnectivityStatus.ONLINE ->
                Text(stringResource(R.string.monitoring_data_online))
            else -> Text(stringResource(R.string.price_loading))
        }
        when {
            uiState.monitoringState.monitoringStatus == MonitoringStatus.SYSTEM_INTERRUPTED ->
                Text(stringResource(R.string.monitoring_status_interrupted), color = MaterialTheme.colorScheme.error)
            uiState.monitoringState.monitoringStatus == MonitoringStatus.LIMITED || batterySaverActive ->
                Text(stringResource(R.string.monitoring_status_limited), color = MaterialTheme.colorScheme.error)
            uiState.monitoringState.monitoringStatus == MonitoringStatus.CONTINUOUS ->
                Text(stringResource(R.string.monitoring_status_continuous))
            uiState.monitoringState.monitoringStatus == MonitoringStatus.WORK_SCHEDULED ->
                Text(stringResource(R.string.monitoring_status_scheduled))
            else -> Text(stringResource(R.string.monitoring_status_stopped))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.continuous_monitoring),
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    stringResource(R.string.continuous_monitoring_description),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Switch(
                checked = uiState.monitoringState.continuousMonitoringEnabled,
                onCheckedChange = onContinuousMonitoringChanged,
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !permissionAllowed) {
            Text(
                stringResource(R.string.notification_permission_message),
                color = MaterialTheme.colorScheme.error,
            )
            TextButton(onClick = onRequestNotificationPermission) {
                Text(stringResource(R.string.request_notification_permission))
            }
        }
    }
}
