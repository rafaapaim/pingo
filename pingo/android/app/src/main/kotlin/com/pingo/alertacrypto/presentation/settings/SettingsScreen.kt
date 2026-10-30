package com.pingo.alertacrypto.presentation.settings

import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
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
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pingo.alertacrypto.BuildConfig
import com.pingo.alertacrypto.R
import com.pingo.alertacrypto.domain.monitoring.ConnectivityStatus
import com.pingo.alertacrypto.domain.monitoring.MonitoringState
import com.pingo.alertacrypto.domain.monitoring.MonitoringStatus
import com.pingo.alertacrypto.presentation.prices.PriceListUiState
import com.pingo.alertacrypto.presentation.theme.PingoTheme
import com.pingo.alertacrypto.presentation.theme.StatusChip
import com.pingo.alertacrypto.presentation.theme.ThemeMode

@Composable
fun SettingsScreen(
    themeMode: ThemeMode,
    monitoringState: MonitoringState,
    priceState: PriceListUiState,
    notificationsAllowed: Boolean,
    notificationRequestAvailable: Boolean,
    onThemeChanged: (ThemeMode) -> Unit,
    onContinuousMonitoringChanged: (Boolean) -> Unit,
    onRequestNotifications: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onReactivateMonitoring: () -> Unit,
) {
    val context = LocalContext.current
    val batteryManager = context.getSystemService(PowerManager::class.java)
    val batterySaverActive = batteryManager?.isPowerSaveMode == true
    val isIgnoringBatteryOptimizations =
        batteryManager?.isIgnoringBatteryOptimizations(context.packageName) == true
    val monitoringDisplay = SettingsStateMapper.monitoring(
        monitoringState.monitoringStatus,
        monitoringState.connectivityStatus,
        batterySaverActive,
    )
    val notificationDisplay = SettingsStateMapper.notification(
        notificationsAllowed,
        notificationRequestAvailable,
    )
    var howItWorksExpanded by remember { mutableStateOf(false) }
    var dataSourcesExpanded by remember { mutableStateOf(false) }
    var disclaimerExpanded by remember { mutableStateOf(false) }
    val latestTime = priceState.assets.mapNotNull { it.tick?.observedAt }.maxOrNull()
        ?.let(com.pingo.alertacrypto.presentation.format.PriceFormatter::time)
        ?: "--:--:--"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Ajustes", style = MaterialTheme.typography.headlineMedium)
        SettingsCard("Aparência") {
            Text("Tema")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeMode.entries.forEach { option ->
                    FilterChip(
                        selected = themeMode == option,
                        onClick = { onThemeChanged(option) },
                        label = { Text(option.label()) },
                    )
                }
            }
            Text(
                "Sistema segue o tema do seu celular.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        SettingsCard("Monitoramento") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        role = Role.Switch,
                        onClick = {
                            onContinuousMonitoringChanged(!monitoringState.continuousMonitoringEnabled)
                        },
                    )
                    .padding(vertical = 4.dp)
                    .semantics(mergeDescendants = true) {},
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Monitoramento contínuo", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Atualiza os preços quase em tempo real, mesmo com o app fechado. Consome mais bateria e o Android pode interromper.",
                    )
                }
                Switch(
                    checked = monitoringState.continuousMonitoringEnabled,
                    onCheckedChange = null,
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatusChip(
                    label = stringResource(monitoringDisplay.labelResource),
                    tone = monitoringDisplay.tone,
                )
                Text(
                    text = stringResource(
                        monitoringDisplay.messageResource,
                        latestTime,
                    ),
                    modifier = Modifier.semantics { liveRegion = androidx.compose.ui.semantics.LiveRegionMode.Polite },
                )
            }
            if (monitoringDisplay.canReactivate) {
                Button(onClick = onReactivateMonitoring) { Text("Reativar") }
            }
            TextButton(onClick = { howItWorksExpanded = !howItWorksExpanded }) {
                Text("Como funciona")
            }
            if (howItWorksExpanded) {
                Text("Contínuo mantém a conexão aberta e mostra uma notificação fixa.")
                Text("Econômico verifica periodicamente, gasta menos bateria e pode perder cruzamentos rápidos.")
            }
        }
        SettingsCard("Notificações") {
            val label = when (notificationDisplay) {
                NotificationDisplay.ENABLED -> "Ativadas"
                else -> "Desativadas"
            }
            val tone = when (notificationDisplay) {
                NotificationDisplay.ENABLED -> com.pingo.alertacrypto.presentation.theme.StatusChipTone.POSITIVE
                else -> com.pingo.alertacrypto.presentation.theme.StatusChipTone.WARNING
            }
            StatusChip(label, tone)
            Text(
                if (notificationsAllowed) {
                    "Você será avisado quando o preço cruzar o valor de um alerta."
                } else {
                    "Sem esta permissão, o Pingo não consegue avisar você quando um alerta disparar."
                },
            )
            Button(
                onClick = {
                    when (notificationDisplay) {
                        NotificationDisplay.ENABLED,
                        NotificationDisplay.SETTINGS_REQUIRED,
                        -> onOpenNotificationSettings()
                        NotificationDisplay.REQUESTABLE -> onRequestNotifications()
                    }
                },
            ) {
                Text(
                    when (notificationDisplay) {
                        NotificationDisplay.ENABLED -> "Gerenciar"
                        NotificationDisplay.REQUESTABLE -> "Permitir notificações"
                        NotificationDisplay.SETTINGS_REQUIRED -> "Abrir configurações"
                    },
                )
            }
        }
        SettingsCard("Bateria") {
            Text("Alguns celulares encerram apps em segundo plano para economizar bateria. Se os alertas atrasarem, permita que o Pingo funcione sem restrições.")
            StatusChip(
                label = if (isIgnoringBatteryOptimizations) "Sem restrições" else "Otimizada",
                tone = if (isIgnoringBatteryOptimizations) {
                    com.pingo.alertacrypto.presentation.theme.StatusChipTone.POSITIVE
                } else {
                    com.pingo.alertacrypto.presentation.theme.StatusChipTone.NEUTRAL
                },
            )
            Button(
                onClick = {
                    context.startActivity(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            .setData(Uri.fromParts("package", context.packageName, null)),
                    )
                },
            ) {
                Text("Abrir configurações do app")
            }
        }
        SettingsCard("Sobre") {
            Text("Versão ${BuildConfig.VERSION_NAME} (build ${BuildConfig.VERSION_CODE})")
            TextButton(
                onClick = onOpenPrivacyPolicy,
            ) { Text("Política de privacidade") }
            TextButton(onClick = { dataSourcesExpanded = !dataSourcesExpanded }) {
                Text("Fontes de dados")
            }
            if (dataSourcesExpanded) {
                Text("Preços: Binance (tempo real) e CoinGecko (reserva). Podem ter atraso ou pequenas diferenças entre si.")
            }
            TextButton(onClick = { disclaimerExpanded = !disclaimerExpanded }) {
                Text("Aviso")
            }
            if (disclaimerExpanded) {
                Text("O Pingo mostra preços e avisa quando eles cruzam valores definidos por você. Não é recomendação de investimento.")
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Pingo", color = MaterialTheme.colorScheme.primary)
            Text(
                "Acompanhe. Monitore. Seja alertado.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SettingsCard(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            content()
        }
    }
}

private fun ThemeMode.label(): String = when (this) {
    ThemeMode.SYSTEM -> "Sistema"
    ThemeMode.LIGHT -> "Claro"
    ThemeMode.DARK -> "Escuro"
}

@Preview(name = "Ajustes claro", showBackground = true)
@Composable
private fun SettingsPreviewLight() = SettingsPreview(false)

@Preview(name = "Ajustes escuro", showBackground = true)
@Composable
private fun SettingsPreviewDark() = SettingsPreview(true)

@Preview(name = "Ajustes claro · notificações negadas", showBackground = true)
@Composable
private fun SettingsNotificationsDeniedPreview() =
    SettingsPreview(false, notificationsAllowed = false)

@Preview(name = "Ajustes escuro · interrompido", showBackground = true)
@Composable
private fun SettingsInterruptedPreview() =
    SettingsPreview(true, monitoringStatus = MonitoringStatus.SYSTEM_INTERRUPTED)

@Preview(name = "Ajustes claro · bateria", showBackground = true)
@Composable
private fun SettingsBatteryLimitedPreview() =
    SettingsPreview(false, monitoringStatus = MonitoringStatus.LIMITED)

@Composable
private fun SettingsPreview(
    darkTheme: Boolean,
    monitoringStatus: MonitoringStatus = MonitoringStatus.WORK_SCHEDULED,
    notificationsAllowed: Boolean = true,
) {
    PingoTheme(darkTheme) {
        SettingsScreen(
            themeMode = if (darkTheme) ThemeMode.DARK else ThemeMode.LIGHT,
            monitoringState = MonitoringState(
                monitoringStatus = monitoringStatus,
                connectivityStatus = ConnectivityStatus.ONLINE,
            ),
            priceState = PriceListUiState(),
            notificationsAllowed = notificationsAllowed,
            notificationRequestAvailable = true,
            onThemeChanged = {},
            onContinuousMonitoringChanged = {},
            onRequestNotifications = {},
            onOpenNotificationSettings = {},
            onOpenPrivacyPolicy = {},
            onReactivateMonitoring = {},
        )
    }
}
