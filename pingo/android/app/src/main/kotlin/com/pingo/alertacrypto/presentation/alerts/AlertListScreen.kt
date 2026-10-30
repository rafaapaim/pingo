package com.pingo.alertacrypto.presentation.alerts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pingo.alertacrypto.R
import com.pingo.alertacrypto.data.notification.AlertNotificationFactory
import com.pingo.alertacrypto.domain.alert.Alert
import com.pingo.alertacrypto.domain.alert.AlertDirection
import com.pingo.alertacrypto.domain.alert.RepeatMode
import com.pingo.alertacrypto.domain.monitoring.QuoteFreshness
import com.pingo.alertacrypto.domain.price.Asset
import com.pingo.alertacrypto.presentation.format.PriceFormatter
import com.pingo.alertacrypto.presentation.prices.PriceListUiState
import com.pingo.alertacrypto.presentation.theme.AssetMark
import com.pingo.alertacrypto.presentation.theme.LocalPingoColors
import com.pingo.alertacrypto.presentation.theme.PingoTheme
import com.pingo.alertacrypto.presentation.theme.StatusChip
import com.pingo.alertacrypto.presentation.theme.StatusChipTone
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Composable
fun AlertListScreen(
    viewModel: AlertListViewModel,
    priceState: PriceListUiState,
    onCreateAlert: () -> Unit,
    onEditAlert: (Alert) -> Unit,
    onRequestNotificationPermission: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    AlertListContent(
        alerts = state.alerts,
        cards = viewModel.projectCards(state.alerts, priceState),
        errorMessage = state.errorMessage,
        notificationsAllowed = AlertNotificationFactory.notificationsAllowed(
            androidx.compose.ui.platform.LocalContext.current,
        ),
        onCreateAlert = onCreateAlert,
        onEditAlert = onEditAlert,
        onToggle = viewModel::setEnabled,
        onDelete = viewModel::delete,
        onRequestNotificationPermission = onRequestNotificationPermission,
    )
}

@Composable
internal fun AlertListContent(
    alerts: List<Alert>,
    cards: List<AlertCardUiModel>,
    errorMessage: String?,
    notificationsAllowed: Boolean,
    onCreateAlert: () -> Unit,
    onEditAlert: (Alert) -> Unit,
    onToggle: (UUID, Boolean) -> Unit,
    onDelete: (UUID) -> Unit,
    onRequestNotificationPermission: () -> Unit,
) {
    Scaffold { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 20.dp),
        ) {
            Text(
                text = stringResource(R.string.alerts_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 8.dp),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(stringResource(R.string.alert_count, alerts.count { it.enabled }))
                Button(onClick = onCreateAlert) {
                    Text(stringResource(R.string.alert_create))
                }
            }
            if (!notificationsAllowed) {
                Text(
                    stringResource(R.string.notification_permission_message),
                    color = MaterialTheme.colorScheme.error,
                )
                TextButton(onClick = onRequestNotificationPermission) {
                    Text(stringResource(R.string.request_notification_permission))
                }
            }
            errorMessage?.let { message ->
                Text(message, color = MaterialTheme.colorScheme.error)
            }
            if (alerts.isEmpty()) {
                Text(
                    stringResource(R.string.alerts_empty),
                    modifier = Modifier.padding(vertical = 12.dp),
                )
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(cards, key = { it.alert.id }) { model ->
                    AlertCard(
                        model = model,
                        onEdit = { onEditAlert(model.alert) },
                        onToggle = { onToggle(model.alert.id, !model.alert.enabled) },
                        onDelete = { onDelete(model.alert.id) },
                    )
                }
            }
        }
    }
}

@Composable
fun AlertCard(
    model: AlertCardUiModel,
    onEdit: () -> Unit = {},
    onToggle: () -> Unit = {},
    onDelete: () -> Unit = {},
) {
    var showDeleteConfirmation by remember(model.alert.id) { mutableStateOf(false) }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Asset.fromSymbol(model.alert.symbol)?.let { AssetMark(it, size = 36.dp) }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "${model.alert.symbol} · ${PriceFormatter.brl(model.alert.targetPriceBRL)}",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        stringResource(
                            when (model.alert.direction) {
                                AlertDirection.UP -> R.string.direction_above
                                AlertDirection.DOWN -> R.string.direction_below
                                AlertDirection.BOTH -> R.string.direction_both
                            },
                        ),
                    )
                }
                StatusChip(
                    label = stringResource(model.status.labelResource()),
                    tone = model.status.tone(),
                )
            }
            model.currentPriceText?.let { current ->
                val stale = model.quoteFreshness == QuoteFreshness.STALE
                Text(
                    text = if (stale) {
                        stringResource(R.string.alert_last_known_price, current)
                    } else {
                        stringResource(R.string.alert_current_price, current)
                    },
                )
            }
            model.distanceText?.let { Text(it, color = MaterialTheme.colorScheme.onSurface) }
            model.reactivationText?.let { Text(it) }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onToggle) {
                    Text(
                        stringResource(
                            if (model.alert.enabled) R.string.alert_disable else R.string.alert_enable,
                        ),
                    )
                }
                TextButton(onClick = onEdit) { Text(stringResource(R.string.alert_edit)) }
                TextButton(onClick = { showDeleteConfirmation = true }) {
                    Text(stringResource(R.string.alert_delete))
                }
            }
        }
    }
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text(stringResource(R.string.alert_delete_confirmation_title)) },
            text = { Text(stringResource(R.string.alert_delete_confirmation_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        onDelete()
                    },
                ) { Text(stringResource(R.string.alert_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

private fun AlertCardStatus.labelResource(): Int = when (this) {
    AlertCardStatus.ACTIVE -> R.string.alert_state_active
    AlertCardStatus.PAUSED -> R.string.alert_state_paused
    AlertCardStatus.WAITING_FOR_REARM -> R.string.alert_state_waiting
    AlertCardStatus.FIRED -> R.string.alert_state_fired
}

private fun AlertCardStatus.tone(): StatusChipTone = when (this) {
    AlertCardStatus.ACTIVE -> StatusChipTone.POSITIVE
    AlertCardStatus.PAUSED -> StatusChipTone.NEUTRAL
    AlertCardStatus.WAITING_FOR_REARM -> StatusChipTone.WARNING
    AlertCardStatus.FIRED -> StatusChipTone.NEGATIVE
}

@Preview(name = "Ativo claro", showBackground = true)
@Composable
private fun AlertCardActiveLightPreview() =
    AlertCardPreview(darkTheme = false, status = AlertCardStatus.ACTIVE)

@Preview(name = "Ativo escuro", showBackground = true)
@Composable
private fun AlertCardActiveDarkPreview() =
    AlertCardPreview(darkTheme = true, status = AlertCardStatus.ACTIVE)

@Preview(name = "Pausado claro", showBackground = true)
@Composable
private fun AlertCardPausedLightPreview() =
    AlertCardPreview(darkTheme = false, status = AlertCardStatus.PAUSED)

@Preview(name = "Pausado escuro", showBackground = true)
@Composable
private fun AlertCardPausedDarkPreview() =
    AlertCardPreview(darkTheme = true, status = AlertCardStatus.PAUSED)

@Preview(name = "Aguardando rearme claro", showBackground = true)
@Composable
private fun AlertCardWaitingLightPreview() =
    AlertCardPreview(darkTheme = false, status = AlertCardStatus.WAITING_FOR_REARM)

@Preview(name = "Aguardando rearme escuro", showBackground = true)
@Composable
private fun AlertCardWaitingDarkPreview() =
    AlertCardPreview(darkTheme = true, status = AlertCardStatus.WAITING_FOR_REARM)

@Preview(name = "Disparado claro", showBackground = true)
@Composable
private fun AlertCardFiredLightPreview() =
    AlertCardPreview(darkTheme = false, status = AlertCardStatus.FIRED)

@Preview(name = "Disparado escuro", showBackground = true)
@Composable
private fun AlertCardFiredDarkPreview() =
    AlertCardPreview(darkTheme = true, status = AlertCardStatus.FIRED)

@Composable
private fun AlertCardPreview(darkTheme: Boolean, status: AlertCardStatus) {
    val alert = Alert(
        id = UUID(0, 1),
        symbol = "BTC",
        targetPriceBRL = BigDecimal("350000"),
        direction = AlertDirection.BOTH,
        repeatMode = if (status == AlertCardStatus.FIRED) RepeatMode.ONE_SHOT
        else RepeatMode.REPEATING,
        armed = status == AlertCardStatus.ACTIVE,
        firedUp = true,
        lastPriceBRL = BigDecimal("351000"),
        enabled = status != AlertCardStatus.PAUSED,
        createdAt = Instant.EPOCH,
        updatedAt = Instant.EPOCH,
        lastFiredAt = Instant.EPOCH,
        lastFiredTickAt = Instant.EPOCH,
    )
    PingoTheme(darkTheme) {
        AlertCard(AlertCardProjector.project(alert, null))
    }
}
