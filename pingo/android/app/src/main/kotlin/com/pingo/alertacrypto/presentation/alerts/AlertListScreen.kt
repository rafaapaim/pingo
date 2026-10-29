package com.pingo.alertacrypto.presentation.alerts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pingo.alertacrypto.R
import com.pingo.alertacrypto.domain.alert.Alert
import com.pingo.alertacrypto.domain.alert.RepeatMode
import com.pingo.alertacrypto.data.notification.AlertNotificationFactory
import com.pingo.alertacrypto.presentation.format.PriceFormatter

@Composable
fun AlertListScreen(
    viewModel: AlertListViewModel,
    onBack: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    var editingAlert by remember { mutableStateOf<Alert?>(null) }
    var isCreating by remember { mutableStateOf(false) }

    if (isCreating || editingAlert != null) {
        AlertEditorScreen(
            initialAlert = editingAlert,
            onCancel = {
                isCreating = false
                editingAlert = null
            },
            onSave = { symbol, target, direction, repeatMode ->
                viewModel.save(editingAlert, symbol, target, direction, repeatMode)
                isCreating = false
                editingAlert = null
            },
        )
        return
    }

    Scaffold { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TextButton(onClick = onBack) { Text(stringResource(R.string.back_to_prices)) }
                Button(onClick = { isCreating = true }) {
                    Text(stringResource(R.string.alert_create))
                }
            }
            Text(
                text = stringResource(R.string.alerts_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 8.dp),
            )
            if (!AlertNotificationFactory.notificationsAllowed(
                    androidx.compose.ui.platform.LocalContext.current,
                )
            ) {
                Text(
                    stringResource(R.string.notification_permission_message),
                    color = MaterialTheme.colorScheme.error,
                )
                TextButton(onClick = onRequestNotificationPermission) {
                    Text(stringResource(R.string.request_notification_permission))
                }
            }
            state.errorMessage?.let { message ->
                Text(message, color = MaterialTheme.colorScheme.error)
            }
            if (state.alerts.isEmpty()) {
                Text(stringResource(R.string.alerts_empty))
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.alerts, key = Alert::id) { alert ->
                    AlertRow(
                        alert = alert,
                        onEdit = { editingAlert = alert },
                        onToggle = { enabled -> viewModel.setEnabled(alert.id, enabled) },
                        onDelete = { viewModel.delete(alert.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AlertRow(
    alert: Alert,
    onEdit: () -> Unit,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                "${alert.symbol} · ${PriceFormatter.brl(alert.targetPriceBRL)}",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                stringResource(
                    R.string.alert_summary,
                    alert.direction.name,
                    if (alert.repeatMode == RepeatMode.ONE_SHOT) {
                        stringResource(R.string.alert_one_shot)
                    } else {
                        stringResource(R.string.alert_repeating)
                    },
                ),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = { onToggle(!alert.enabled) }) {
                    Text(
                        stringResource(
                            if (alert.enabled) R.string.alert_disable else R.string.alert_enable,
                        ),
                    )
                }
                TextButton(onClick = onEdit) { Text(stringResource(R.string.alert_edit)) }
                TextButton(onClick = onDelete) { Text(stringResource(R.string.alert_delete)) }
            }
        }
    }
}
