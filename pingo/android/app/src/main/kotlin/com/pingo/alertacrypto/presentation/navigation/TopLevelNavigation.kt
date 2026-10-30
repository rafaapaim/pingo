package com.pingo.alertacrypto.presentation.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.pingo.alertacrypto.R

internal data class TopLevelDestination(
    val route: String,
    val labelResource: Int,
    val iconResource: Int,
)

internal val topLevelDestinations = listOf(
    TopLevelDestination(PingoRoutes.MARKET, R.string.nav_market, R.drawable.ic_market),
    TopLevelDestination(PingoRoutes.ALERTS, R.string.nav_alerts, R.drawable.ic_alerts),
    TopLevelDestination(PingoRoutes.SETTINGS, R.string.nav_settings, R.drawable.ic_settings),
)

@Composable
internal fun TopLevelNavigationBar(
    currentRoute: String?,
    onDestinationSelected: (String) -> Unit,
) {
    NavigationBar {
        topLevelDestinations.forEach { destination ->
            NavigationBarItem(
                selected = currentRoute == destination.route,
                onClick = { onDestinationSelected(destination.route) },
                modifier = Modifier.testTag("navigation_${destination.route}"),
                icon = {
                    Icon(
                        painter = painterResource(destination.iconResource),
                        contentDescription = null,
                    )
                },
                label = { Text(stringResource(destination.labelResource)) },
            )
        }
    }
}

internal fun NavHostController.navigateToTopLevelDestination(route: String) {
    require(topLevelDestinations.any { it.route == route }) {
        "Not a top-level destination: $route"
    }
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
