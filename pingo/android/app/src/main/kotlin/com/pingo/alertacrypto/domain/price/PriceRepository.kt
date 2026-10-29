package com.pingo.alertacrypto.domain.price

import kotlinx.coroutines.flow.Flow

sealed interface PriceUpdate {
    data class Tick(
        val value: PriceTick,
        val sourceId: String,
    ) : PriceUpdate

    data class Unavailable(val reason: String) : PriceUpdate
}

interface PriceRepository {
    fun observePrices(): Flow<PriceUpdate>
}
