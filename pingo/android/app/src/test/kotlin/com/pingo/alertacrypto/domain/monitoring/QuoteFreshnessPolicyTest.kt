package com.pingo.alertacrypto.domain.monitoring

import org.junit.Assert.assertEquals
import org.junit.Test

class QuoteFreshnessPolicyTest {
    @Test
    fun activeWebSocketQuoteIsFreshAtThirtySecondsAndStaleAfter() {
        val policy = QuoteFreshnessPolicy

        assertEquals(
            QuoteFreshness.FRESH,
            policy.classify(QuoteSourceKind.WEBSOCKET_ACTIVE, receivedAtMillis = 1_000L, nowMillis = 31_000L),
        )
        assertEquals(
            QuoteFreshness.STALE,
            policy.classify(QuoteSourceKind.WEBSOCKET_ACTIVE, receivedAtMillis = 1_000L, nowMillis = 31_001L),
        )
    }

    @Test
    fun restWorkerQuoteIsFreshAtTwentyMinutesAndStaleAfter() {
        val policy = QuoteFreshnessPolicy

        assertEquals(
            QuoteFreshness.FRESH,
            policy.classify(QuoteSourceKind.REST_WORKER, receivedAtMillis = 0L, nowMillis = 1_200_000L),
        )
        assertEquals(
            QuoteFreshness.STALE,
            policy.classify(QuoteSourceKind.REST_WORKER, receivedAtMillis = 0L, nowMillis = 1_200_001L),
        )
    }

    @Test
    fun widgetQuoteIsFreshAtThirtyMinutesAndStaleAfter() {
        val policy = QuoteFreshnessPolicy

        assertEquals(
            QuoteFreshness.FRESH,
            policy.classify(QuoteSourceKind.WIDGET, receivedAtMillis = 0L, nowMillis = 1_800_000L),
        )
        assertEquals(
            QuoteFreshness.STALE,
            policy.classify(QuoteSourceKind.WIDGET, receivedAtMillis = 0L, nowMillis = 1_800_001L),
        )
    }

    @Test
    fun absentAndFutureQuotesAreNotReportedFresh() {
        assertEquals(
            QuoteFreshness.MISSING,
            QuoteFreshnessPolicy.classify(QuoteSourceKind.WIDGET, null, nowMillis = 10L),
        )
        assertEquals(
            QuoteFreshness.STALE,
            QuoteFreshnessPolicy.classify(QuoteSourceKind.WIDGET, 11L, nowMillis = 10L),
        )
    }
}
