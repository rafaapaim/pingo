package com.pingo.alertacrypto.domain.alert

import com.pingo.alertacrypto.domain.price.PriceTick

fun interface ProcessPriceTick {
    suspend fun process(tick: PriceTick, sourceId: String): TickProcessingResult
}

data class TickProcessingResult(
    val accepted: Boolean,
    val eventsCreated: Int,
)
