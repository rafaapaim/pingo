package com.pingo.alertacrypto.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pingo.alertacrypto.R
import com.pingo.alertacrypto.domain.monitoring.QuoteFreshness
import com.pingo.alertacrypto.domain.price.Asset
import com.pingo.alertacrypto.presentation.format.PriceFormatter
import com.pingo.alertacrypto.presentation.prices.AssetPrice
import com.pingo.alertacrypto.presentation.prices.PriceListViewModel
import com.pingo.alertacrypto.presentation.theme.AssetMark
import com.pingo.alertacrypto.presentation.theme.LocalPingoColors
import com.pingo.alertacrypto.presentation.theme.PingoTheme

@Composable
fun PriceDetailScreen(
    symbol: String,
    viewModel: PriceListViewModel,
    onBack: () -> Unit,
    onCreateAlert: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    val row = state.assets.firstOrNull { it.asset.symbol == symbol }
    val asset = Asset.fromSymbol(symbol)
    if (asset == null) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(stringResource(R.string.deep_link_unknown_asset))
            Button(onClick = onBack) { Text(stringResource(R.string.nav_market)) }
        }
    } else {
        PriceDetailContent(
            asset = asset,
            price = row,
            onCreateAlert = onCreateAlert,
        )
    }
}

@Composable
internal fun PriceDetailContent(
    asset: Asset,
    price: AssetPrice?,
    onCreateAlert: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AssetMark(asset = asset, size = 72.dp)
        Text(asset.displayName, style = MaterialTheme.typography.headlineMedium)
        Text(asset.symbol, style = MaterialTheme.typography.titleMedium)
        val tick = price?.tick
        Text(
            text = tick?.let { PriceFormatter.brl(it.priceBRL) }
                ?: stringResource(R.string.price_not_available),
            style = MaterialTheme.typography.headlineLarge,
        )
        tick?.change24hPercent?.let { change ->
            Text(
                text = PriceFormatter.percent(change),
                color = if (change.signum() >= 0) {
                    LocalPingoColors.current.positiveVariation
                } else {
                    LocalPingoColors.current.negativeVariation
                },
                style = MaterialTheme.typography.titleMedium,
            )
        } ?: Text(stringResource(R.string.price_change_unavailable))
        tick?.let {
            val freshness = price.freshness
            Text(
                text = stringResource(
                    if (freshness == QuoteFreshness.STALE) R.string.price_last_known else R.string.price_last_updated,
                    if (freshness == QuoteFreshness.STALE) PriceFormatter.brl(it.priceBRL)
                    else PriceFormatter.time(it.observedAt),
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Button(onClick = onCreateAlert) {
            Text(stringResource(R.string.alert_create))
        }
    }
}

@Preview(name = "Detalhe claro · atualizado", showBackground = true)
@Composable
private fun PriceDetailFreshLightPreview() =
    PriceDetailPreview(darkTheme = false, freshness = QuoteFreshness.FRESH)

@Preview(name = "Detalhe escuro · atualizado", showBackground = true)
@Composable
private fun PriceDetailFreshDarkPreview() =
    PriceDetailPreview(darkTheme = true, freshness = QuoteFreshness.FRESH)

@Composable
@Preview(name = "Detalhe claro · sem cotação", showBackground = true)
private fun PriceDetailMissingLightPreview() =
    PriceDetailPreview(darkTheme = false, freshness = QuoteFreshness.MISSING)

@Preview(name = "Detalhe escuro · sem cotação", showBackground = true)
@Composable
private fun PriceDetailMissingDarkPreview() =
    PriceDetailPreview(darkTheme = true, freshness = QuoteFreshness.MISSING)

@Preview(name = "Detalhe claro · desatualizado", showBackground = true)
@Composable
private fun PriceDetailStaleLightPreview() =
    PriceDetailPreview(darkTheme = false, freshness = QuoteFreshness.STALE)

@Preview(name = "Detalhe escuro · desatualizado", showBackground = true)
@Composable
private fun PriceDetailStaleDarkPreview() =
    PriceDetailPreview(darkTheme = true, freshness = QuoteFreshness.STALE)

@Composable
private fun PriceDetailPreview(darkTheme: Boolean, freshness: QuoteFreshness) {
    PingoTheme(darkTheme) {
        PriceDetailContent(
            asset = Asset.BTC,
            price = if (freshness == QuoteFreshness.MISSING) {
                null
            } else {
                AssetPrice(
                    asset = Asset.BTC,
                    tick = com.pingo.alertacrypto.domain.price.PriceTick(
                        symbol = "BTC",
                        priceBRL = java.math.BigDecimal("350000.00"),
                        change24hPercent = java.math.BigDecimal("1.25"),
                        observedAt = java.time.Instant.parse("2026-10-30T12:00:00Z"),
                    ),
                    freshness = freshness,
                )
            },
            onCreateAlert = {},
        )
    }
}
