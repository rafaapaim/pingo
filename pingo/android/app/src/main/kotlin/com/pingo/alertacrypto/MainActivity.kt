package com.pingo.alertacrypto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val priceListViewModel: PriceListViewModel by viewModels()
    private val alertListViewModel: AlertListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemDarkTheme = isSystemInDarkTheme()
            var darkTheme by rememberSaveable { mutableStateOf(systemDarkTheme) }
            var showAlerts by rememberSaveable { mutableStateOf(false) }
            PingoTheme(darkTheme = darkTheme) {
                Surface {
                    if (showAlerts) {
                        AlertListScreen(
                            viewModel = alertListViewModel,
                            onBack = { showAlerts = false },
                        )
                    } else {
                        PriceListScreen(
                            viewModel = priceListViewModel,
                            darkTheme = darkTheme,
                            onToggleTheme = { darkTheme = !darkTheme },
                            onOpenAlerts = { showAlerts = true },
                        )
                    }
                }
            }
        }
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
