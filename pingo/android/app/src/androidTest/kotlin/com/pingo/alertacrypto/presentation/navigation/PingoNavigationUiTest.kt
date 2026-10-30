package com.pingo.alertacrypto.presentation.navigation

import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertTopPositionInRootIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.statusBars
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pingo.alertacrypto.presentation.theme.PingoTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PingoNavigationUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun navigationMovesFromMarketToAssetAndBack() {
        composeRule.setContent {
            PingoTheme(darkTheme = false) {
                NavigationTestHost()
            }
        }

        composeRule.onNodeWithText("Mercado").assertExists()
        composeRule.onNodeWithText("Abrir BTC").performClick()
        composeRule.onNodeWithText("Detalhe BTC").assertExists()
        composeRule.onNodeWithText("Voltar").performClick()
        composeRule.onNodeWithText("Mercado").assertExists()
    }

    @Test
    fun changingSettingDoesNotPreventReturningToMarketFromBottomNavigation() {
        composeRule.setContent {
            PingoTheme(darkTheme = false) {
                TopLevelNavigationTestHost()
            }
        }

        composeRule.onNodeWithTag("navigation_settings").performClick()
        composeRule.onNodeWithText("Ajustes da conta").assertIsDisplayed()
        composeRule.onNodeWithText("Tema: Sistema").assertIsDisplayed()
        composeRule.onNodeWithText("Escuro").performClick()
        composeRule.onNodeWithText("Tema: Escuro").assertIsDisplayed()

        composeRule.onNodeWithTag("navigation_market").performClick()
        composeRule.onNodeWithText("Tela Mercado").assertIsDisplayed()
        composeRule.onNodeWithTag("navigation_market").assertIsSelected()

        composeRule.onNodeWithTag("navigation_settings").performClick()
        composeRule.onNodeWithText("Tema: Escuro").assertIsDisplayed()
        composeRule.onNodeWithTag("navigation_settings").assertIsSelected()
    }

    @Test
    fun switchingPrimaryDestinationsRepeatedlyKeepsTheSelectedDestinationInSync() {
        composeRule.setContent {
            PingoTheme(darkTheme = false) {
                TopLevelNavigationTestHost()
            }
        }

        listOf(
            "navigation_alerts" to "Tela Alertas",
            "navigation_settings" to "Ajustes da conta",
            "navigation_market" to "Tela Mercado",
            "navigation_settings" to "Ajustes da conta",
            "navigation_alerts" to "Tela Alertas",
            "navigation_market" to "Tela Mercado",
        ).forEach { (navigationTag, screenLabel) ->
            composeRule.onNodeWithTag(navigationTag).performClick()
            composeRule.onNodeWithText(screenLabel).assertIsDisplayed()
            composeRule.onNodeWithTag(navigationTag).assertIsSelected()
        }
    }

    @Test
    fun sharedHeaderRespectsStatusBarInsetAcrossPrimaryDestinations() {
        composeRule.setContent {
            PingoTheme(darkTheme = false) {
                TopLevelNavigationTestHost(statusBarInsets = WindowInsets(top = 32.dp))
            }
        }

        @Test
        fun sharedHeaderStartsBelowTheReportedStatusBarInset() {
            var statusBarInsetPx = 0
            composeRule.setContent {
                val density = LocalDensity.current
                statusBarInsetPx = WindowInsets.statusBars.getTop(density)
                PingoTheme(darkTheme = false) {
                    TopLevelNavigationTestHost(statusBarInsets = WindowInsets.statusBars)
                }
            }

            composeRule.waitForIdle()
            val expectedTop = with(composeRule.density) {
                statusBarInsetPx.toDp() + 12.dp
            }
            composeRule.onNodeWithContentDescription("Pingo")
                .assertTopPositionInRootIsEqualTo(expectedTop)

            listOf(
                "navigation_alerts" to "Tela Alertas",
                "navigation_settings" to "Ajustes da conta",
                "navigation_market" to "Tela Mercado",
            ).forEach { (navigationTag, screenLabel) ->
                composeRule.onNodeWithTag(navigationTag).performClick()
                composeRule.onNodeWithText(screenLabel).assertIsDisplayed()
                composeRule.onNodeWithContentDescription("Pingo")
                    .assertTopPositionInRootIsEqualTo(expectedTop)
            }
        }

        composeRule.onNodeWithContentDescription("Pingo")
            .assertTopPositionInRootIsEqualTo(44.dp)

        listOf(
            "navigation_alerts" to "Tela Alertas",
            "navigation_settings" to "Ajustes da conta",
            "navigation_market" to "Tela Mercado",
        ).forEach { (navigationTag, screenLabel) ->
            composeRule.onNodeWithTag(navigationTag).performClick()
            composeRule.onNodeWithText(screenLabel).assertIsDisplayed()
            composeRule.onNodeWithContentDescription("Pingo")
                .assertTopPositionInRootIsEqualTo(44.dp)
        }
    }

    @Composable
    private fun TopLevelNavigationTestHost(
        statusBarInsets: WindowInsets = WindowInsets(top = 0.dp),
    ) {
        val navController = rememberNavController()
        var theme by remember { mutableStateOf("Sistema") }
        val backStackEntry by navController.currentBackStackEntryAsState()

        Scaffold(
            topBar = { PingoAppHeader(statusBarInsets = statusBarInsets) },
            bottomBar = {
                TopLevelNavigationBar(
                    currentRoute = backStackEntry?.destination?.route,
                    onDestinationSelected = navController::navigateToTopLevelDestination,
                )
            },
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = PingoRoutes.MARKET,
                modifier = Modifier.padding(padding),
            ) {
                composable(PingoRoutes.MARKET) { Text("Tela Mercado") }
                composable(PingoRoutes.ALERTS) { Text("Tela Alertas") }
                composable(PingoRoutes.SETTINGS) {
                    Column {
                        Text("Ajustes da conta")
                        Text("Tema: $theme")
                        TextButton(onClick = { theme = "Escuro" }) {
                            Text("Escuro")
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun NavigationTestHost() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = PingoRoutes.MARKET) {
            composable(PingoRoutes.MARKET) {
                Button(onClick = { navController.navigate(PingoRoutes.assetDetails("BTC")) }) {
                    Text("Abrir BTC")
                }
                Text("Mercado")
            }
            composable(PingoRoutes.ASSET_DETAILS) {
                Button(onClick = { navController.popBackStack() }) {
                    Text("Voltar")
                }
                Text("Detalhe BTC")
            }
        }
    }
}
