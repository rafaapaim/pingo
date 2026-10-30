package com.pingo.alertacrypto.presentation.prices

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import com.pingo.alertacrypto.R
import com.pingo.alertacrypto.domain.monitoring.ConnectivityStatus
import com.pingo.alertacrypto.domain.monitoring.MonitoringStatus
import com.pingo.alertacrypto.domain.monitoring.QuoteFreshness
import com.pingo.alertacrypto.domain.price.Asset
import com.pingo.alertacrypto.domain.price.PriceTick
import com.pingo.alertacrypto.presentation.format.PriceFormatter
import com.pingo.alertacrypto.presentation.theme.AssetMark
import com.pingo.alertacrypto.presentation.theme.FavoriteAssetOrdering
import com.pingo.alertacrypto.presentation.theme.LocalPingoColors
import com.pingo.alertacrypto.presentation.theme.PingoTheme
import com.pingo.alertacrypto.presentation.theme.StatusChip
import com.pingo.alertacrypto.presentation.theme.StatusChipTone
import java.math.BigDecimal
import java.time.Instant

@Composable
fun PriceListScreen(
    viewModel: PriceListViewModel,
    onOpenAsset: (String) -> Unit,
    onOpenSettings: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    PriceListContent(
        uiState = uiState,
        onOpenAsset = onOpenAsset,
        onFavorite = viewModel::setFavorite,
        onOpenSettings = onOpenSettings,
    )
}

@Composable
internal fun PriceListContent(
    uiState: PriceListUiState,
    onOpenAsset: (String) -> Unit,
    onFavorite: (String, Boolean) -> Unit,
    onOpenSettings: () -> Unit,
    initialSearchQuery: String = "",
) {
    var searchQuery by remember { mutableStateOf(initialSearchQuery) }
    val visibleAssets = FavoriteAssetOrdering.order(uiState.favoriteSymbols)
        .mapNotNull { asset -> uiState.assets.firstOrNull { it.asset == asset } }
        .filter { row ->
            row.asset.symbol.contains(searchQuery, ignoreCase = true) ||
                row.asset.displayName.contains(searchQuery, ignoreCase = true)
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.price_list_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 4.dp),
        )
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text(stringResource(R.string.search_hint)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("market_search"),
        )
        MarketStatus(
            uiState = uiState,
            onOpenSettings = onOpenSettings,
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f).fillMaxWidth().testTag("market_assets"),
        ) {
            if (visibleAssets.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.search_no_results),
                        modifier = Modifier.padding(vertical = 16.dp),
                    )
                }
            }
            items(visibleAssets, key = { it.asset.symbol }) { row ->
                val isFavorite = row.asset.symbol in uiState.favoriteSymbols
                Card(
                    onClick = { onOpenAsset(row.asset.symbol) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, top = 8.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AssetMark(asset = row.asset, size = 36.dp)
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = row.asset.displayName,
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.titleSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = " · ${row.asset.symbol}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = row.tick?.let { tick ->
                                        val price = PriceFormatter.brl(tick.priceBRL)
                                        if (uiState.connectionState == PriceConnectionState.OFFLINE ||
                                            row.freshness != QuoteFreshness.FRESH
                                        ) {
                                            stringResource(R.string.price_last_known, price)
                                        } else {
                                            price
                                        }
                                    } ?: stringResource(R.string.price_not_available),
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Spacer(Modifier.weight(1f))
                                row.tick?.let { tick ->
                                    val change = tick.change24hPercent
                                    Text(
                                        text = change?.let(PriceFormatter::percent)
                                            ?: stringResource(R.string.price_change_unavailable),
                                        color = change?.let {
                                            if (it.signum() >= 0) {
                                                LocalPingoColors.current.positiveVariation
                                            } else {
                                                LocalPingoColors.current.negativeVariation
                                            }
                                        } ?: MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 1,
                                    )
                                }
                            }
                        }
                        IconButton(
                            onClick = { onFavorite(row.asset.symbol, !isFavorite) },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_favorite),
                                contentDescription = stringResource(
                                    if (isFavorite) R.string.favorite_remove else R.string.favorite_add,
                                    row.asset.displayName,
                                ),
                                tint = if (isFavorite) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MarketStatus(
    uiState: PriceListUiState,
    onOpenSettings: () -> Unit,
) {
    val stale = uiState.assets.any { it.freshness == QuoteFreshness.STALE }
    val offline = uiState.connectionState == PriceConnectionState.OFFLINE ||
        uiState.monitoringState.connectivityStatus == ConnectivityStatus.OFFLINE
    val (label, tone) = when {
        offline -> stringResource(R.string.market_status_offline) to StatusChipTone.NEGATIVE
        stale -> stringResource(R.string.market_status_stale) to StatusChipTone.WARNING
        uiState.connectionState == PriceConnectionState.LOADING ->
            stringResource(R.string.market_status_loading) to StatusChipTone.NEUTRAL
        uiState.assets.all { it.freshness == QuoteFreshness.MISSING } ->
            stringResource(R.string.market_status_waiting) to StatusChipTone.NEUTRAL
        else -> stringResource(R.string.market_status_online) to StatusChipTone.POSITIVE
    }
    val monitoringLabel = when (uiState.monitoringState.monitoringStatus) {
        MonitoringStatus.CONTINUOUS -> stringResource(R.string.settings_status_active)
        MonitoringStatus.WORK_SCHEDULED -> stringResource(R.string.settings_status_economic)
        MonitoringStatus.SYSTEM_INTERRUPTED -> stringResource(R.string.settings_status_interrupted)
        MonitoringStatus.LIMITED, MonitoringStatus.STOPPED ->
            stringResource(R.string.settings_status_paused)
    }
    val lastUpdated = uiState.assets.mapNotNull { it.tick?.observedAt }.maxOrNull()
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatusChip(label = label, tone = tone)
            val palette = LocalPingoColors.current.neutralChip
            Box(
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .clip(CircleShape)
                    .background(palette.container)
                    .clickable(role = Role.Button, onClick = onOpenSettings)
                    .testTag("market_monitoring_settings")
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.market_monitoring_chip, monitoringLabel),
                    color = palette.foreground,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        lastUpdated?.let { observedAt ->
            Text(
                text = stringResource(
                    R.string.price_last_updated,
                    PriceFormatter.time(observedAt),
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(name = "Mercado claro · online", showBackground = true)
@Composable
private fun MarketLightPreview() = MarketPreview(
    darkTheme = false,
    connectionState = PriceConnectionState.ONLINE,
)

@Preview(name = "Mercado escuro · offline", showBackground = true)
@Composable
private fun MarketDarkPreview() = MarketPreview(
    darkTheme = true,
    connectionState = PriceConnectionState.OFFLINE,
)

@Preview(name = "Mercado claro · carregando", showBackground = true)
@Composable
private fun MarketLoadingPreview() = MarketPreview(
    darkTheme = false,
    connectionState = PriceConnectionState.LOADING,
)

@Preview(name = "Mercado escuro · sem resultados", showBackground = true)
@Composable
private fun MarketNoResultsPreview() = MarketPreview(
    darkTheme = true,
    connectionState = PriceConnectionState.ONLINE,
    searchQuery = "moeda inexistente",
)

@Preview(name = "Mercado claro · favoritos", showBackground = true)
@Composable
private fun MarketFavoritesPreview() = MarketPreview(
    darkTheme = false,
    connectionState = PriceConnectionState.ONLINE,
    favoriteSymbols = setOf("ADA", "BTC"),
)

@Preview(name = "Mercado escuro · favoritos", showBackground = true)
@Composable
private fun MarketDarkFavoritesPreview() = MarketPreview(
    darkTheme = true,
    connectionState = PriceConnectionState.ONLINE,
    favoriteSymbols = setOf("ADA", "BTC"),
)

@Composable
private fun MarketPreview(
    darkTheme: Boolean,
    connectionState: PriceConnectionState,
    searchQuery: String = "",
    favoriteSymbols: Set<String> = emptySet(),
) {
    val sample = AssetPrice(
        asset = Asset.BTC,
        tick = PriceTick(
            symbol = "BTC",
            priceBRL = BigDecimal("350000.00"),
            change24hPercent = BigDecimal("1.25"),
            observedAt = Instant.parse("2026-10-30T12:00:00Z"),
        ),
        freshness = if (connectionState == PriceConnectionState.OFFLINE) {
            QuoteFreshness.STALE
        } else {
            QuoteFreshness.FRESH
        },
    )
    PingoTheme(darkTheme) {
        PriceListContent(
            uiState = PriceListUiState(
                assets = Asset.supported.map { asset ->
                    if (asset == Asset.BTC) sample else AssetPrice(asset = asset)
                },
                favoriteSymbols = favoriteSymbols,
                connectionState = connectionState,
            ),
            onOpenAsset = {},
            onFavorite = { _, _ -> },
            onOpenSettings = {},
            initialSearchQuery = searchQuery,
        )
    }
}
