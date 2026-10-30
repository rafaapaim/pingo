package com.pingo.alertacrypto

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.pingo.alertacrypto.data.monitoring.MonitoringController
import com.pingo.alertacrypto.data.notification.AlertNotificationFactory
import com.pingo.alertacrypto.presentation.alerts.AlertListViewModel
import com.pingo.alertacrypto.presentation.navigation.PingoAppNavigation
import com.pingo.alertacrypto.presentation.navigation.PriceDetailDeepLink
import com.pingo.alertacrypto.presentation.prices.PriceListViewModel
import com.pingo.alertacrypto.presentation.theme.PingoTheme
import com.pingo.alertacrypto.presentation.theme.ThemeMode
import com.pingo.alertacrypto.presentation.theme.ThemePreferenceStore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var monitoringController: MonitoringController

    private val priceListViewModel: PriceListViewModel by viewModels()
    private val alertListViewModel: AlertListViewModel by viewModels()
    private val themePreferenceStore by lazy { ThemePreferenceStore(applicationContext) }
    private var incomingIntent by mutableStateOf<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AlertNotificationFactory(this)
        setContent {
            val systemDarkTheme = isSystemInDarkTheme()
            val themeMode by themePreferenceStore.themeMode.collectAsState(
                initial = ThemeMode.SYSTEM,
            )
            val darkTheme = themeMode.resolvesDarkTheme(systemDarkTheme)
            val scope = rememberCoroutineScope()
            var enableContinuousAfterPermission by rememberSaveable { mutableStateOf(false) }
            var notificationPermissionDenied by rememberSaveable { mutableStateOf(false) }
            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission(),
            ) { granted ->
                notificationPermissionDenied = !granted
                if (enableContinuousAfterPermission) {
                    monitoringController.setContinuousEnabled(granted)
                    enableContinuousAfterPermission = false
                }
            }

            PingoTheme(darkTheme = darkTheme) {
                Surface {
                    PingoAppNavigation(
                        priceListViewModel = priceListViewModel,
                        alertListViewModel = alertListViewModel,
                        themeMode = themeMode,
                        darkTheme = darkTheme,
                        notificationRequestAvailable = !notificationPermissionDenied,
                        initialIntent = intent.takeIf(PriceDetailDeepLink::isDeepLinkIntent),
                        incomingIntent = incomingIntent,
                        onThemeModeChanged = { selectedMode ->
                            scope.launch {
                                themePreferenceStore.setThemeMode(selectedMode)
                            }
                        },
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
                        onOpenNotificationSettings = ::openNotificationSettings,
                        onOpenPrivacyPolicy = {
                            Toast.makeText(
                                this,
                                R.string.privacy_policy_not_published,
                                Toast.LENGTH_LONG,
                            ).show()
                        },
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        incomingIntent = intent
    }

    private fun openNotificationSettings() {
        startActivity(
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, packageName),
        )
    }
}
