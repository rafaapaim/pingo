package com.pingo.alertacrypto.presentation.prices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pingo.alertacrypto.domain.price.Asset
import com.pingo.alertacrypto.domain.price.PriceRepository
import com.pingo.alertacrypto.domain.price.PriceTick
import com.pingo.alertacrypto.domain.price.PriceUpdate
import dagger.hilt.android.lifecycle.HiltViewModel
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
)

data class PriceListUiState(
    val assets: List<AssetPrice> = Asset.supported.map(::AssetPrice),
    val connectionState: PriceConnectionState = PriceConnectionState.LOADING,
)

@HiltViewModel
class PriceListViewModel @Inject constructor(
    repository: PriceRepository,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(PriceListUiState())
    val uiState = mutableUiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observePrices().collect(::onPriceUpdate)
        }
    }

    private fun onPriceUpdate(update: PriceUpdate) {
        when (update) {
            is PriceUpdate.Tick -> mutableUiState.value = mutableUiState.value.copy(
                assets = mutableUiState.value.assets.map { row ->
                    if (row.asset.symbol == update.value.symbol) row.copy(tick = update.value) else row
                },
                connectionState = PriceConnectionState.ONLINE,
            )

            is PriceUpdate.Unavailable -> mutableUiState.value = mutableUiState.value.copy(
                connectionState = PriceConnectionState.OFFLINE,
            )
        }
    }
}
