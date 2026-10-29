package com.pingo.alertacrypto.presentation.alerts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pingo.alertacrypto.R
import com.pingo.alertacrypto.domain.alert.Alert
import com.pingo.alertacrypto.domain.alert.AlertDirection
import com.pingo.alertacrypto.domain.alert.RepeatMode
import com.pingo.alertacrypto.domain.price.Asset
import java.math.BigDecimal

@Composable
fun AlertEditorScreen(
    initialAlert: Alert?,
    onCancel: () -> Unit,
    onSave: (String, BigDecimal, AlertDirection, RepeatMode) -> Unit,
) {
    var symbol by remember(initialAlert) { mutableStateOf(initialAlert?.symbol ?: "BTC") }
    var target by remember(initialAlert) {
        mutableStateOf(initialAlert?.targetPriceBRL?.toPlainString().orEmpty())
    }
    var direction by remember(initialAlert) {
        mutableStateOf(initialAlert?.direction ?: AlertDirection.UP)
    }
    var repeatMode by remember(initialAlert) {
        mutableStateOf(initialAlert?.repeatMode ?: RepeatMode.REPEATING)
    }
    var symbolMenuExpanded by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(
                if (initialAlert == null) R.string.alert_create else R.string.alert_edit,
            ),
            style = MaterialTheme.typography.headlineSmall,
        )
        TextButton(onClick = { symbolMenuExpanded = true }) {
            Text(stringResource(R.string.alert_currency, symbol))
        }
        androidx.compose.material3.DropdownMenu(
            expanded = symbolMenuExpanded,
            onDismissRequest = { symbolMenuExpanded = false },
        ) {
            Asset.supported.forEach { asset ->
                androidx.compose.material3.DropdownMenuItem(
                    text = { Text("${asset.symbol} · ${asset.displayName}") },
                    onClick = {
                        symbol = asset.symbol
                        symbolMenuExpanded = false
                    },
                )
            }
        }
        androidx.compose.material3.OutlinedTextField(
            value = target,
            onValueChange = { target = it; validationError = false },
            label = { Text(stringResource(R.string.alert_target_price)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(stringResource(R.string.alert_direction))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            AlertDirection.entries.forEach { option ->
                TextButton(onClick = { direction = option }) {
                    Text(
                        text = stringResource(option.labelResource()),
                        color = if (direction == option) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                    )
                }
            }
        }
        Text(stringResource(R.string.alert_repeat_mode))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            RepeatMode.entries.forEach { option ->
                TextButton(onClick = { repeatMode = option }) {
                    Text(
                        text = stringResource(
                            if (option == RepeatMode.ONE_SHOT) {
                                R.string.alert_one_shot
                            } else {
                                R.string.alert_repeating
                            },
                        ),
                        color = if (repeatMode == option) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                    )
                }
            }
        }
        if (validationError) {
            Text(
                stringResource(R.string.alert_invalid_target),
                color = MaterialTheme.colorScheme.error,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = onCancel) { Text(stringResource(R.string.cancel)) }
            Button(
                onClick = {
                    val targetValue = parseBrazilianDecimal(target)
                    if (targetValue == null || targetValue <= BigDecimal.ZERO) {
                        validationError = true
                    } else {
                        onSave(symbol, targetValue, direction, repeatMode)
                    }
                },
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}

private fun AlertDirection.labelResource(): Int = when (this) {
    AlertDirection.UP -> R.string.direction_up
    AlertDirection.DOWN -> R.string.direction_down
    AlertDirection.BOTH -> R.string.direction_both
}

private fun parseBrazilianDecimal(value: String): BigDecimal? {
    val normalized = value.trim()
        .removePrefix("R$")
        .replace(" ", "")
        .let { input ->
            if (input.contains(',')) input.replace(".", "").replace(',', '.') else input
        }
    if (!normalized.matches(Regex("\\d+(\\.\\d+)?"))) return null
    return normalized.toBigDecimalOrNull()
}
