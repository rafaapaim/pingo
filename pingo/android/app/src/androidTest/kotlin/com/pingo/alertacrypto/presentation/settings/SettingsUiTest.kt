package com.pingo.alertacrypto.presentation.settings

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pingo.alertacrypto.domain.monitoring.ConnectivityStatus
import com.pingo.alertacrypto.domain.monitoring.MonitoringState
import com.pingo.alertacrypto.domain.monitoring.MonitoringStatus
import com.pingo.alertacrypto.presentation.prices.PriceListUiState
import com.pingo.alertacrypto.presentation.theme.PingoTheme
import com.pingo.alertacrypto.presentation.theme.ThemeMode
import java.util.concurrent.atomic.AtomicReference
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun rendersFiveSettingsCardsAndPersistsThemeSelectionThroughCallback() {
        val selected = AtomicReference<ThemeMode>()
        composeRule.setContent {
            PingoTheme(darkTheme = false) {
                SettingsScreen(
                    themeMode = ThemeMode.SYSTEM,
                    monitoringState = MonitoringState(
                        monitoringStatus = MonitoringStatus.WORK_SCHEDULED,
                        connectivityStatus = ConnectivityStatus.ONLINE,
                    ),
                    priceState = PriceListUiState(),
                    notificationsAllowed = false,
                    notificationRequestAvailable = true,
                    onThemeChanged = selected::set,
                    onContinuousMonitoringChanged = {},
                    onRequestNotifications = {},
                    onOpenNotificationSettings = {},
                    onOpenPrivacyPolicy = {},
                    onReactivateMonitoring = {},
                )
            }
        }

        listOf("Aparência", "Monitoramento", "Notificações", "Bateria", "Sobre").forEach {
            composeRule.onNodeWithText(it).assertExists()
        }
        composeRule.onNodeWithText("Escuro").performClick()
        assertEquals(ThemeMode.DARK, selected.get())
    }
}
