package com.pingo.alertacrypto.data.local.entity

import com.pingo.alertacrypto.domain.price.PriceTick
import java.math.BigDecimal
import java.time.Instant

fun LatestPriceEntity.toPriceTick(): PriceTick = PriceTick(
    symbol = symbol,
    priceBRL = BigDecimal(priceBRL),
    change24hPercent = change24hPercent?.let(::BigDecimal),
    observedAt = Instant.ofEpochMilli(observedAtEpochMillis),
)
