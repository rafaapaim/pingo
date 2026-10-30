package com.pingo.alertacrypto.presentation.alerts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pingo.alertacrypto.R
import com.pingo.alertacrypto.domain.alert.Alert
import com.pingo.alertacrypto.domain.alert.AlertDirection
import com.pingo.alertacrypto.domain.alert.RepeatMode
import com.pingo.alertacrypto.domain.price.Asset
import com.pingo.alertacrypto.presentation.theme.PingoTheme
import java.math.BigDecimal

@Composable
fun AlertEditorScreen(
    initialAlert: Alert?,
    initialSymbol: String? = null,
    currentPriceText: String? = null,
    onCancel: () -> Unit,
    onSave: (String, BigDecimal, AlertDirection, RepeatMode) -> Unit,
) {
    var symbol by remember(initialAlert, initialSymbol) {
        mutableStateOf(initialAlert?.symbol ?: initialSymbol ?: "BTC")
    }
    var target by remember(initialAlert) {
        mutableStateOf(initialAlert?.targetPriceBRL?.toPlainString().orEmpty())
    }
    var direction by remember(initialAlert) {
        mutableStateOf(initialAlert?.direction ?: AlertDirection.UP)
    }
    var repeats by remember(initialAlert) {
        mutableStateOf(initialAlert?.repeatMode != RepeatMode.ONE_SHOT)
    }
    var symbolMenuExpanded by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(
                if (initialAlert == null) R.string.alert_create else R.string.alert_edit,
            ),
            style = MaterialTheme.typography.headlineMedium,
        )
        TextButton(onClick = { symbolMenuExpanded = true }) {
            Text(stringResource(R.string.alert_currency, symbol))
        }
        DropdownMenu(
            expanded = symbolMenuExpanded,
            onDismissRequest = { symbolMenuExpanded = false },
        ) {
            Asset.supported.forEach { asset ->
                DropdownMenuItem(
                    text = { Text("${asset.symbol} · ${asset.displayName}") },
                    onClick = {
                        symbol = asset.symbol
                        symbolMenuExpanded = false
                    },
                )
            }
        }
        currentPriceText?.let { Text(stringResource(R.string.alert_current_price, it)) }
        OutlinedTextField(
            value = target,
            onValueChange = { target = it; validationError = false },
            label = { Text(stringResource(R.string.alert_target_price)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            isError = validationError,
            modifier = Modifier.fillMaxWidth().testTag("alert_target"),
        )
        Text(stringResource(R.string.alert_direction))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AlertDirection.entries.forEach { option ->
                FilterChip(
                    selected = direction == option,
                    onClick = { direction = option },
                    label = { Text(stringResource(option.labelResource())) },
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { repeats = !repeats }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.alert_repeat_mode))
                Text(
                    stringResource(R.string.alert_repeat_explanation),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(
                checked = repeats,
                onCheckedChange = { repeats = it },
            )
        }
        if (validationError) {
            Text(
                stringResource(R.string.alert_invalid_target),
                color = MaterialTheme.colorScheme.error,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
        ) {
            TextButton(onClick = onCancel) { Text(stringResource(R.string.cancel)) }
            Button(
                onClick = {
                    val targetValue = parseBrazilianDecimal(target)
                    if (targetValue == null || targetValue <= BigDecimal.ZERO) {
                        validationError = true
                    } else {
                        onSave(
                            symbol,
                            targetValue,
                            direction,
                            if (repeats) RepeatMode.REPEATING else RepeatMode.ONE_SHOT,
                        )
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

internal fun parseBrazilianDecimal(value: String): BigDecimal? {
    val normalized = value.trim()
        .removePrefix("R$")
        .replace(" ", "")
        .let { input ->
            if (input.contains(',')) input.replace(".", "").replace(',', '.') else input
        }
    if (!normalized.matches(Regex("\\d+(\\.\\d+)?"))) return null
    return normalized.toBigDecimalOrNull()
}

@Preview(name = "Criar alerta claro", showBackground = true)
@Composable
private fun AlertEditorPreviewLight() = AlertEditorPreview(false)

@Preview(name = "Criar alerta escuro", showBackground = true)
@Composable
private fun AlertEditorPreviewDark() = AlertEditorPreview(true)

@Composable
private fun AlertEditorPreview(darkTheme: Boolean) {
    PingoTheme(darkTheme) {
        AlertEditorScreen(
            initialAlert = null,
            onCancel = {},
            onSave = { _, _, _, _ -> },
        )
    }
}
