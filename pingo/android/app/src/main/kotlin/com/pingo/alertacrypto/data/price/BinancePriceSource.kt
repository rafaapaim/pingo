package com.pingo.alertacrypto.data.price

import com.pingo.alertacrypto.domain.price.Asset
import com.pingo.alertacrypto.domain.price.PriceTick
import com.pingo.alertacrypto.domain.price.StreamingPriceSource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.math.BigDecimal
import java.time.Instant

class BinancePriceSource(
    private val client: OkHttpClient,
) : StreamingPriceSource {
    override suspend fun fetchLatest(symbols: Set<String>): List<PriceTick> = withContext(Dispatchers.IO) {
        val assets = validateSymbols(symbols)
        val symbolsJson = JSONArray(assets.map(Asset::binanceSymbol)).toString()
        val url = "$REST_BASE_URL/api/v3/ticker/24hr".toHttpUrl()
            .newBuilder()
            .addQueryParameter("symbols", symbolsJson)
            .build()
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Binance REST returned HTTP ${response.code}")
            }
            val payload = response.body.string()
            if (payload.isBlank()) {
                throw IOException("Binance REST returned an empty response")
            }
            parseRestPayload(payload)
        }
    }

    override fun observeTicks(symbols: Set<String>): Flow<PriceTick> = callbackFlow {
        val assets = try {
            validateSymbols(symbols)
        } catch (error: IllegalArgumentException) {
            close(error)
            return@callbackFlow
        }
        val streams = assets.joinToString("/") { "${it.binanceSymbol.lowercase()}@ticker" }
        val httpsUrl = "$REST_BASE_URL/stream".toHttpUrl()
            .newBuilder()
            .addQueryParameter("streams", streams)
            .build()
        val request = Request.Builder()
            .url(httpsUrl.toString().replaceFirst("https://", "wss://"))
            .build()
        val socket = client.newWebSocket(
            request,
            object : WebSocketListener() {
                override fun onMessage(webSocket: WebSocket, text: String) {
                    try {
                        trySend(parseStreamPayload(text))
                    } catch (error: JSONException) {
                        close(IOException("Invalid Binance WebSocket payload", error))
                    } catch (error: IllegalArgumentException) {
                        close(IOException("Invalid Binance ticker data", error))
                    }
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    close(IOException("Binance WebSocket failed", t))
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    close(IOException("Binance WebSocket closed ($code): $reason"))
                }
            },
        )
        awaitClose { socket.cancel() }
    }

    private fun parseRestPayload(payload: String): List<PriceTick> {
        val rows = JSONArray(payload)
        return buildList(rows.length()) {
            for (index in 0 until rows.length()) {
                val row = rows.getJSONObject(index)
                val asset = assetForBinanceSymbol(row.getString("symbol"))
                add(
                    PriceTick(
                        symbol = asset.symbol,
                        priceBRL = row.getString("lastPrice").toBigDecimal(),
                        change24hPercent = row.getString("priceChangePercent").toBigDecimal(),
                        observedAt = Instant.ofEpochMilli(row.getLong("closeTime")),
                    ),
                )
            }
        }
    }

    private fun parseStreamPayload(payload: String): PriceTick {
        val data = JSONObject(payload).getJSONObject("data")
        val asset = assetForBinanceSymbol(data.getString("s"))
        return PriceTick(
            symbol = asset.symbol,
            priceBRL = data.getString("c").toBigDecimal(),
            change24hPercent = data.getString("P").toBigDecimal(),
            observedAt = Instant.ofEpochMilli(data.getLong("E")),
        )
    }

    private fun assetForBinanceSymbol(symbol: String): Asset =
        Asset.supported.firstOrNull { it.binanceSymbol == symbol }
            ?: throw IllegalArgumentException("Unsupported Binance symbol: $symbol")

    private fun validateSymbols(symbols: Set<String>): List<Asset> {
        require(symbols.isNotEmpty()) { "At least one asset symbol is required" }
        return symbols.map { symbol ->
            Asset.fromSymbol(symbol)
                ?: throw IllegalArgumentException("Unsupported asset symbol: $symbol")
        }
    }

    companion object {
        private const val REST_BASE_URL = "https://api.binance.com"
    }
}
