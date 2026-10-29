package com.pingo.alertacrypto.presentation.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pingo.alertacrypto.R
import com.pingo.alertacrypto.domain.price.Asset
import com.pingo.alertacrypto.presentation.format.PriceFormatter
import com.pingo.alertacrypto.presentation.prices.PriceListViewModel

@Composable
fun PriceDetailScreen(
    symbol: String,
    viewModel: PriceListViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val row = state.assets.firstOrNull { it.asset.symbol == symbol }
    val asset = row?.asset ?: Asset.fromSymbol(symbol)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        TextButton(onClick = onBack) { Text(stringResource(R.string.back_to_prices)) }
        Text(
            text = asset?.displayName ?: symbol,
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(symbol, style = MaterialTheme.typography.titleMedium)
        Text(
            text = row?.tick?.let { PriceFormatter.brl(it.priceBRL) }
                ?: stringResource(R.string.price_not_available),
            style = MaterialTheme.typography.headlineSmall,
        )
        row?.tick?.let { tick ->
            Text(
                tick.change24hPercent?.let(PriceFormatter::percent)
                    ?: stringResource(R.string.price_change_unavailable),
            )
            Text(
                stringResource(
                    R.string.price_last_updated,
                    PriceFormatter.time(tick.observedAt),
                ),
            )
        }
    }
}
