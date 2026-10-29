package com.pingo.alertacrypto.domain.monitoring

import java.util.concurrent.TimeUnit

enum class QuoteFreshness {
    FRESH,
    STALE,
    MISSING,
}

enum class QuoteSourceKind {
    WEBSOCKET_ACTIVE,
    REST_WORKER,
    WIDGET,
}

object QuoteFreshnessPolicy {
    val WEBSOCKET_ACTIVE_STALE_AFTER: Long = TimeUnit.SECONDS.toMillis(30)
    val REST_WORKER_STALE_AFTER: Long = TimeUnit.MINUTES.toMillis(20)
    val WIDGET_STALE_AFTER: Long = TimeUnit.MINUTES.toMillis(30)

    fun classify(
        sourceKind: QuoteSourceKind,
        receivedAtMillis: Long?,
        nowMillis: Long,
    ): QuoteFreshness {
        if (receivedAtMillis == null) return QuoteFreshness.MISSING
        val ageMillis = nowMillis - receivedAtMillis
        if (ageMillis < 0L) return QuoteFreshness.STALE
        val threshold = when (sourceKind) {
            QuoteSourceKind.WEBSOCKET_ACTIVE -> WEBSOCKET_ACTIVE_STALE_AFTER
            QuoteSourceKind.REST_WORKER -> REST_WORKER_STALE_AFTER
            QuoteSourceKind.WIDGET -> WIDGET_STALE_AFTER
        }
        return if (ageMillis <= threshold) QuoteFreshness.FRESH else QuoteFreshness.STALE
    }
}
