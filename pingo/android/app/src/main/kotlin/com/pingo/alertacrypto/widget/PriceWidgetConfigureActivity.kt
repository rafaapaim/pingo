package com.pingo.alertacrypto.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.pingo.alertacrypto.R
import com.pingo.alertacrypto.domain.price.Asset
import kotlinx.coroutines.launch

class PriceWidgetConfigureActivity : ComponentActivity() {
    private var selectedSymbol by mutableStateOf(PriceGlanceWidget.DEFAULT_SYMBOL)
    private var menuExpanded by mutableStateOf(false)
    private var appWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        setResult(
            Activity.RESULT_CANCELED,
            Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId),
        )
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContent {
            MaterialTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        stringResource(R.string.widget_config_title),
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    TextButton(onClick = { menuExpanded = true }) {
                        Text(stringResource(R.string.widget_config_symbol, selectedSymbol))
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                    ) {
                        Asset.supported.forEach { asset ->
                            DropdownMenuItem(
                                text = { Text("${asset.symbol} · ${asset.displayName}") },
                                onClick = {
                                    selectedSymbol = asset.symbol
                                    menuExpanded = false
                                },
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TextButton(onClick = { finish() }) {
                            Text(stringResource(R.string.widget_config_cancel))
                        }
                        Button(onClick = ::saveConfiguration) {
                            Text(stringResource(R.string.widget_config_add))
                        }
                    }
                }
            }
        }
    }

    private fun saveConfiguration() {
        lifecycleScope.launch {
            val glanceId = GlanceAppWidgetManager(this@PriceWidgetConfigureActivity)
                .getGlanceIdBy(appWidgetId)
            updateAppWidgetState(this@PriceWidgetConfigureActivity, glanceId) { preferences ->
                preferences[PriceWidgetSymbolKey] = selectedSymbol
            }
            PriceGlanceWidget().update(this@PriceWidgetConfigureActivity, glanceId)
            setResult(
                Activity.RESULT_OK,
                Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId),
            )
            finish()
        }
    }
}
