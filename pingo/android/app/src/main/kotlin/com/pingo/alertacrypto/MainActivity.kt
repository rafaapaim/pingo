package com.pingo.alertacrypto

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.pingo.alertacrypto.presentation.prices.PriceListScreen
import com.pingo.alertacrypto.presentation.prices.PriceListViewModel
import com.pingo.alertacrypto.presentation.alerts.AlertListScreen
import com.pingo.alertacrypto.presentation.alerts.AlertListViewModel
import com.pingo.alertacrypto.data.monitoring.MonitoringController
import com.pingo.alertacrypto.data.notification.AlertNotificationFactory
import com.pingo.alertacrypto.presentation.navigation.PriceDetailDestination
import com.pingo.alertacrypto.presentation.navigation.PriceDetailScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var monitoringController: MonitoringController

    private val priceListViewModel: PriceListViewModel by viewModels()
    private val alertListViewModel: AlertListViewModel by viewModels()
    private var detailSymbol by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailSymbol = intent.getStringExtra(PriceDetailDestination.EXTRA_SYMBOL)
        AlertNotificationFactory(this)
        setContent {
            val systemDarkTheme = isSystemInDarkTheme()
            var darkTheme by rememberSaveable { mutableStateOf(systemDarkTheme) }
            var showAlerts by rememberSaveable { mutableStateOf(false) }
            var enableContinuousAfterPermission by rememberSaveable { mutableStateOf(false) }
            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission(),
            ) { granted ->
                if (enableContinuousAfterPermission) {
                    monitoringController.setContinuousEnabled(granted)
                    enableContinuousAfterPermission = false
                }
            }
            PingoTheme(darkTheme = darkTheme) {
                Surface {
                    if (detailSymbol != null) {
                        PriceDetailScreen(
                            symbol = detailSymbol.orEmpty(),
                            viewModel = priceListViewModel,
                            onBack = { detailSymbol = null },
                        )
                    } else if (showAlerts) {
                        AlertListScreen(
                            viewModel = alertListViewModel,
                            onBack = { showAlerts = false },
                            onRequestNotificationPermission = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    enableContinuousAfterPermission = false
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    openNotificationSettings()
                                }
                            },
                        )
                    } else {
                        PriceListScreen(
                            viewModel = priceListViewModel,
                            darkTheme = darkTheme,
                            onToggleTheme = { darkTheme = !darkTheme },
                            onOpenAlerts = { showAlerts = true },
                            onContinuousMonitoringChanged = { enabled ->
                                if (!enabled) {
                                    monitoringController.setContinuousEnabled(false)
                                } else if (
                                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                    ContextCompat.checkSelfPermission(
                                        this,
                                        Manifest.permission.POST_NOTIFICATIONS,
                                    ) != PackageManager.PERMISSION_GRANTED
                                ) {
                                    enableContinuousAfterPermission = true
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else if (!AlertNotificationFactory.notificationsAllowed(this)) {
                                    openNotificationSettings()
                                } else {
                                    monitoringController.setContinuousEnabled(true)
                                }
                            },
                            onRequestNotificationPermission = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    enableContinuousAfterPermission = false
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    openNotificationSettings()
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        detailSymbol = intent.getStringExtra(PriceDetailDestination.EXTRA_SYMBOL)
    }

    private fun openNotificationSettings() {
        startActivity(
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, packageName),
        )
    }
}

@Composable
private fun PingoTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme(),
        content = content,
    )
}
