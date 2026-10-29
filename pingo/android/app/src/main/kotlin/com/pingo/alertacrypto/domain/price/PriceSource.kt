package com.pingo.alertacrypto.domain.price

import kotlinx.coroutines.flow.Flow

interface PriceSource {
    suspend fun fetchLatest(symbols: Set<String>): List<PriceTick>
}

interface StreamingPriceSource : PriceSource {
    fun observeTicks(symbols: Set<String>): Flow<PriceTick>
}
