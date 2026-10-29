package com.pingo.alertacrypto.presentation.prices

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pingo.alertacrypto.R
import com.pingo.alertacrypto.presentation.format.PriceFormatter

@Composable
fun PriceListScreen(
    viewModel: PriceListViewModel,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onOpenAlerts: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedSymbol by remember { mutableStateOf<String?>(null) }
    val visibleAssets = uiState.assets.filter { row ->
        row.asset.symbol.contains(searchQuery, ignoreCase = true) ||
            row.asset.displayName.contains(searchQuery, ignoreCase = true)
    }

    Scaffold { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.price_list_title),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
                TextButton(onClick = onToggleTheme) {
                    Text(
                        stringResource(
                            if (darkTheme) R.string.theme_dark else R.string.theme_light,
                        ),
                    )
                }
                TextButton(onClick = onOpenAlerts) {
                    Text(stringResource(R.string.alerts_title))
                }
            }
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text(stringResource(R.string.search_hint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            when (uiState.connectionState) {
                PriceConnectionState.LOADING -> Text(
                    text = stringResource(R.string.price_loading),
                    modifier = Modifier.padding(vertical = 8.dp),
                )
                PriceConnectionState.OFFLINE -> Text(
                    text = stringResource(R.string.price_offline),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
                PriceConnectionState.ONLINE -> Unit
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(visibleAssets, key = { it.asset.symbol }) { row ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedSymbol = row.asset.symbol }
                            .padding(vertical = 8.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = "${row.asset.symbol} · ${row.asset.displayName}",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            if (selectedSymbol == row.asset.symbol) {
                                Text(
                                    text = stringResource(R.string.asset_selected),
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                            Text(
                                text = row.tick?.let { tick ->
                                    val price = PriceFormatter.brl(tick.priceBRL)
                                    if (uiState.connectionState == PriceConnectionState.OFFLINE) {
                                        "${stringResource(R.string.price_last_known)}: $price"
                                    } else {
                                        price
                                    }
                                }
                                    ?: stringResource(R.string.price_not_available),
                            )
                        }
                        row.tick?.let { tick ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = tick.change24hPercent?.let(PriceFormatter::percent)
                                        ?: stringResource(R.string.price_change_unavailable),
                                )
                                Text(
                                    text = stringResource(
                                        R.string.price_last_updated,
                                        PriceFormatter.time(tick.observedAt),
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
