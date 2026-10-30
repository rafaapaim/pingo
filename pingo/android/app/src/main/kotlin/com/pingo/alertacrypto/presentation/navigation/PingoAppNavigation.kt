package com.pingo.alertacrypto.presentation.navigation

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pingo.alertacrypto.R
import com.pingo.alertacrypto.presentation.alerts.AlertEditorScreen
import com.pingo.alertacrypto.presentation.alerts.AlertListViewModel
import com.pingo.alertacrypto.presentation.alerts.AlertListScreen
import com.pingo.alertacrypto.presentation.format.PriceFormatter
import com.pingo.alertacrypto.presentation.prices.PriceListViewModel
import com.pingo.alertacrypto.presentation.prices.PriceListScreen
import com.pingo.alertacrypto.presentation.settings.SettingsScreen
import com.pingo.alertacrypto.presentation.theme.ThemeMode
import java.util.UUID

@Composable
fun PingoAppNavigation(
    priceListViewModel: PriceListViewModel,
    alertListViewModel: AlertListViewModel,
    themeMode: ThemeMode,
    darkTheme: Boolean,
    notificationRequestAvailable: Boolean,
    initialIntent: Intent?,
    incomingIntent: Intent?,
    onThemeModeChanged: (ThemeMode) -> Unit,
    onContinuousMonitoringChanged: (Boolean) -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
) {
    val navController = rememberNavController()
    val priceState by priceListViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    fun deliverIntent(intent: Intent) {
        if (!PriceDetailDeepLink.isDeepLinkIntent(intent)) return
        val symbol = PriceDetailDeepLink.supportedSymbol(intent)
        if (symbol == null) {
            navController.navigate(PingoRoutes.MARKET) {
                popUpTo(PingoRoutes.MARKET)
                launchSingleTop = true
            }
            Toast.makeText(context, R.string.deep_link_unknown_asset, Toast.LENGTH_SHORT).show()
        } else {
            navController.navigate(PingoRoutes.assetDetails(symbol)) {
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(initialIntent) {
        initialIntent?.let(::deliverIntent)
    }
    LaunchedEffect(incomingIntent) {
        incomingIntent?.let(::deliverIntent)
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val primaryDestinations = setOf(
        PingoRoutes.MARKET,
        PingoRoutes.ALERTS,
        PingoRoutes.SETTINGS,
    )
    val showBottomBar = currentRoute in primaryDestinations

    Scaffold(
        topBar = {
            PingoAppHeader()
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                TopLevelNavigationBar(
                    currentRoute = currentRoute,
                    onDestinationSelected = navController::navigateToTopLevelDestination,
                )
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = PingoRoutes.MARKET,
            modifier = Modifier.padding(padding),
        ) {
            composable(PingoRoutes.MARKET) {
                PriceListScreen(
                    viewModel = priceListViewModel,
                    onOpenAsset = { symbol ->
                        navController.navigate(PingoRoutes.assetDetails(symbol))
                    },
                    onOpenSettings = {
                        navController.navigateToTopLevelDestination(PingoRoutes.SETTINGS)
                    },
                )
            }
            composable(PingoRoutes.ALERTS) {
                AlertListScreen(
                    viewModel = alertListViewModel,
                    priceState = priceState,
                    onCreateAlert = {
                        navController.navigate(PingoRoutes.alertEditor())
                    },
                    onEditAlert = { alert ->
                        navController.navigate(PingoRoutes.alertEditor(alertId = alert.id.toString()))
                    },
                    onRequestNotificationPermission = onRequestNotificationPermission,
                )
            }
            composable(PingoRoutes.SETTINGS) {
                SettingsScreen(
                    themeMode = themeMode,
                    monitoringState = priceState.monitoringState,
                    priceState = priceState,
                    notificationsAllowed = androidx.compose.ui.platform.LocalContext.current.let {
                        com.pingo.alertacrypto.data.notification.AlertNotificationFactory
                            .notificationsAllowed(it)
                    },
                    notificationRequestAvailable = notificationRequestAvailable,
                    onThemeChanged = onThemeModeChanged,
                    onContinuousMonitoringChanged = onContinuousMonitoringChanged,
                    onRequestNotifications = onRequestNotificationPermission,
                    onOpenNotificationSettings = onOpenNotificationSettings,
                    onOpenPrivacyPolicy = onOpenPrivacyPolicy,
                    onReactivateMonitoring = { onContinuousMonitoringChanged(true) },
                )
            }
            composable(
                route = PingoRoutes.ASSET_DETAILS,
                arguments = listOf(navArgument(PingoRoutes.SYMBOL_ARGUMENT) {
                    type = NavType.StringType
                }),
                deepLinks = listOf(navDeepLink {
                    uriPattern = PingoRoutes.ASSET_DEEP_LINK
                }),
            ) { entry ->
                val symbol = entry.arguments?.getString(PingoRoutes.SYMBOL_ARGUMENT).orEmpty()
                PriceDetailScreen(
                    symbol = symbol,
                    viewModel = priceListViewModel,
                    onBack = { navController.popBackStack() },
                    onCreateAlert = {
                        navController.navigate(
                            PingoRoutes.alertEditor(symbol = symbol),
                        )
                    },
                )
            }
            composable(
                route = PingoRoutes.ALERT_EDITOR,
                arguments = listOf(
                    navArgument(PingoRoutes.ALERT_ID_ARGUMENT) {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument(PingoRoutes.SYMBOL_ARGUMENT) {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                ),
            ) { entry ->
                val alertState by alertListViewModel.uiState.collectAsState()
                val id = entry.arguments?.getString(PingoRoutes.ALERT_ID_ARGUMENT)
                    ?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                val existing = alertState.alerts.firstOrNull { it.id == id }
                val symbol = entry.arguments?.getString(PingoRoutes.SYMBOL_ARGUMENT)
                    ?: existing?.symbol
                AlertEditorScreen(
                    initialAlert = existing,
                    initialSymbol = symbol,
                    currentPriceText = priceState.assets
                        .firstOrNull { it.asset.symbol == symbol }?.tick
                        ?.let { PriceFormatter.brl(it.priceBRL) },
                    onCancel = { navController.popBackStack() },
                    onSave = { assetSymbol, target, direction, repeatMode ->
                        alertListViewModel.save(existing, assetSymbol, target, direction, repeatMode)
                        navController.popBackStack()
                    },
                )
            }
        }
    }
}
