package com.pingo.alertacrypto.domain.price

import java.math.BigDecimal
import java.time.Instant

data class PriceTick(
    val symbol: String,
    val priceBRL: BigDecimal,
    val change24hPercent: BigDecimal?,
    val observedAt: Instant,
) {
    init {
        require(Asset.fromSymbol(symbol) != null) { "Unsupported asset symbol: $symbol" }
        require(priceBRL.signum() > 0) { "Price must be positive" }
    }
}
