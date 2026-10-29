package com.pingo.alertacrypto.presentation.prices

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pingo.alertacrypto.domain.price.Asset
import com.pingo.alertacrypto.domain.price.PriceRepository
import com.pingo.alertacrypto.domain.price.PriceTick
import com.pingo.alertacrypto.domain.price.PriceUpdate
import com.pingo.alertacrypto.data.local.dao.LatestPriceDao
import com.pingo.alertacrypto.data.local.entity.toPriceTick
import com.pingo.alertacrypto.data.alert.PriceTickProcessor
import com.pingo.alertacrypto.data.monitoring.MonitoringStateStore
import com.pingo.alertacrypto.data.notification.AlertNotificationDispatcher
import com.pingo.alertacrypto.data.notification.AlertNotificationFactory
import com.pingo.alertacrypto.domain.monitoring.ConnectivityStatus
import com.pingo.alertacrypto.domain.monitoring.MonitoringState
import com.pingo.alertacrypto.domain.monitoring.MonitoringStatus
import com.pingo.alertacrypto.domain.monitoring.QuoteFreshness
import com.pingo.alertacrypto.domain.monitoring.QuoteFreshnessPolicy
import com.pingo.alertacrypto.domain.monitoring.QuoteSourceKind
import java.time.Instant
import kotlinx.coroutines.delay
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class PriceConnectionState {
    LOADING,
    ONLINE,
    OFFLINE,
}

data class AssetPrice(
    val asset: Asset,
    val tick: PriceTick? = null,
    val sourceId: String? = null,
    val receivedAtMillis: Long? = null,
    val freshness: QuoteFreshness = QuoteFreshness.MISSING,
)

data class PriceListUiState(
    val assets: List<AssetPrice> = Asset.supported.map(::AssetPrice),
    val connectionState: PriceConnectionState = PriceConnectionState.LOADING,
    val monitoringState: MonitoringState = MonitoringState(),
    val nowMillis: Long = System.currentTimeMillis(),
)

@HiltViewModel
class PriceListViewModel @Inject constructor(
    repository: PriceRepository,
    @ApplicationContext private val context: Context,
    private val latestPriceDao: LatestPriceDao,
    private val processor: PriceTickProcessor,
    private val notificationDispatcher: AlertNotificationDispatcher,
    private val monitoringStateStore: MonitoringStateStore,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(PriceListUiState())
    val uiState = mutableUiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observePrices().collect(::onPriceUpdate)
        }
        viewModelScope.launch {
            latestPriceDao.observeAll().collect { latest ->
                val bySymbol = latest.associateBy { it.symbol }
                mutableUiState.value = mutableUiState.value.copy(
                    assets = mutableUiState.value.assets.map { row ->
                        val cached = bySymbol[row.asset.symbol]
                        if (cached == null) row else {
                            val receivedAt = cached.receivedAtEpochMillis
                            val source = when (cached.sourceId) {
                                "binance_ws" -> if (
                                    mutableUiState.value.connectionState == PriceConnectionState.ONLINE ||
                                    mutableUiState.value.monitoringState.monitoringStatus == MonitoringStatus.CONTINUOUS
                                ) QuoteSourceKind.WEBSOCKET_ACTIVE else QuoteSourceKind.REST_WORKER
                                else -> QuoteSourceKind.REST_WORKER
                            }
                            row.copy(
                                tick = cached.toPriceTick(),
                                sourceId = cached.sourceId,
                                receivedAtMillis = receivedAt,
                                freshness = QuoteFreshnessPolicy.classify(
                                    source,
                                    receivedAt,
                                    System.currentTimeMillis(),
                                ),
                            )
                        }
                    },
                    nowMillis = System.currentTimeMillis(),
                )
            }
        }
        viewModelScope.launch {
            monitoringStateStore.state.collect { monitoring ->
                mutableUiState.value = mutableUiState.value.copy(monitoringState = monitoring)
            }
        }
        viewModelScope.launch {
            while (true) {
                delay(FRESHNESS_REFRESH_MILLIS)
                val now = System.currentTimeMillis()
                mutableUiState.value = mutableUiState.value.copy(
                    nowMillis = now,
                    assets = mutableUiState.value.assets.map { row ->
                        val source = when (row.sourceId) {
                            "binance_ws" -> if (
                                mutableUiState.value.connectionState == PriceConnectionState.ONLINE ||
                                mutableUiState.value.monitoringState.monitoringStatus == MonitoringStatus.CONTINUOUS
                            ) QuoteSourceKind.WEBSOCKET_ACTIVE else QuoteSourceKind.REST_WORKER
                            else -> QuoteSourceKind.REST_WORKER
                        }
                        row.copy(
                            freshness = QuoteFreshnessPolicy.classify(
                                source,
                                row.receivedAtMillis,
                                now,
                            ),
                        )
                    },
                )
            }
        }
    }

    private fun onPriceUpdate(update: PriceUpdate) {
        when (update) {
            is PriceUpdate.Tick -> {
                mutableUiState.value = mutableUiState.value.copy(
                    assets = mutableUiState.value.assets.map { row ->
                        if (row.asset.symbol == update.value.symbol) row.copy(
                            tick = update.value,
                            sourceId = update.sourceId,
                            receivedAtMillis = System.currentTimeMillis(),
                            freshness = QuoteFreshness.FRESH,
                        ) else row
                    },
                    connectionState = PriceConnectionState.ONLINE,
                )
                viewModelScope.launch {
                    processor.process(update.value, update.sourceId)
                    val result = notificationDispatcher.dispatchPending(
                        AlertNotificationFactory.notificationsAllowed(context),
                    )
                    result.failures.forEach { failure ->
                        Log.e("PriceListViewModel", "Failed to publish alert ${failure.eventId}", failure.cause)
                    }
                }
            }

            is PriceUpdate.Unavailable -> mutableUiState.value = mutableUiState.value.copy(
                connectionState = PriceConnectionState.OFFLINE,
            )
        }
    }

    private companion object {
        const val FRESHNESS_REFRESH_MILLIS = 5_000L
    }
}
