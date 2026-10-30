package com.pingo.alertacrypto.data.price

import com.pingo.alertacrypto.domain.price.Asset
import java.math.BigDecimal
import java.time.Instant
import okhttp3.OkHttpClient
import org.json.JSONException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class PriceSourceMapperTest {
    private val binance = BinancePriceSource(OkHttpClient())
    private val coinGecko = CoinGeckoPriceSource(UnusedCoinGeckoApi)

    @Test
    fun mapsBinanceRestTickerToCanonicalPriceTick() {
        val tick = binance.parseRestPayload(
            """[{"symbol":"BTCBRL","lastPrice":"1234.56","priceChangePercent":"1.25","closeTime":1767225600000}]""",
        ).single()

        assertEquals("BTC", tick.symbol)
        assertEquals(BigDecimal("1234.56"), tick.priceBRL)
        assertEquals(BigDecimal("1.25"), tick.change24hPercent)
        assertEquals(Instant.ofEpochMilli(1767225600000), tick.observedAt)
    }

    @Test
    fun mapsBinanceCombinedStreamTickerToCanonicalPriceTick() {
        val tick = binance.parseStreamPayload(
            """{"stream":"btCbrl@ticker","data":{"s":"BTCBRL","c":"1234.56","P":"1.25","E":1767225600000}}""",
        )

        assertEquals("BTC", tick.symbol)
        assertEquals(BigDecimal("1234.56"), tick.priceBRL)
        assertEquals(BigDecimal("1.25"), tick.change24hPercent)
        assertEquals(Instant.ofEpochMilli(1767225600000), tick.observedAt)
    }

    @Test
    fun rejectsUnsupportedBinanceSymbolAndMalformedPayload() {
        assertThrows(IllegalArgumentException::class.java) {
            binance.parseStreamPayload(
                """{"data":{"s":"UNKNOWNBRL","c":"10","P":"1","E":1767225600000}}""",
            )
        }
        assertThrows(JSONException::class.java) {
            binance.parseRestPayload("not-json")
        }
    }

    @Test
    fun mapsCoinGeckoBrlPriceChangeAndUpdatedAt() {
        val tick = coinGecko.parsePayload(
            """{"bitcoin":{"brl":1234.56,"brl_24h_change":1.25,"last_updated_at":1767225600}}""",
            listOf(Asset.BTC),
        ).single()

        assertEquals("BTC", tick.symbol)
        assertEquals(BigDecimal("1234.56"), tick.priceBRL)
        assertEquals(BigDecimal("1.25"), tick.change24hPercent)
        assertEquals(Instant.ofEpochSecond(1767225600), tick.observedAt)
    }

    @Test
    fun toleratesMissingCoinGeckoVariationAndRejectsInvalidPriceOrTimestamp() {
        val tick = coinGecko.parsePayload(
            """{"ethereum":{"brl":2500.00,"last_updated_at":1767225600}}""",
            listOf(Asset.ETH),
        ).single()
        assertEquals(null, tick.change24hPercent)

        assertThrows(java.io.IOException::class.java) {
            coinGecko.parsePayload(
                """{"ethereum":{"brl":"invalid","last_updated_at":1767225600}}""",
                listOf(Asset.ETH),
            )
        }
        assertThrows(java.io.IOException::class.java) {
            coinGecko.parsePayload(
                """{"ethereum":{"brl":2500.00,"last_updated_at":0}}""",
                listOf(Asset.ETH),
            )
        }
    }

    @Test
    fun rejectsUnsupportedRequestedAssetsBeforeCallingCoinGecko() {
        assertThrows(IllegalArgumentException::class.java) {
            kotlinx.coroutines.runBlocking { coinGecko.fetchLatest(setOf("NOPE")) }
        }
    }

    private object UnusedCoinGeckoApi : CoinGeckoPriceSource.Api {
        override suspend fun fetchPrices(
            ids: String,
            currencies: String,
            includeChange: Boolean,
            includeUpdatedAt: Boolean,
        ): retrofit2.Response<okhttp3.ResponseBody> =
            error("The parser test must not make an API request.")
    }
}
