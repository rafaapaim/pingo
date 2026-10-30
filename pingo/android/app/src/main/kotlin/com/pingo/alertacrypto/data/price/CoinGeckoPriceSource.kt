package com.pingo.alertacrypto.data.price

import com.pingo.alertacrypto.domain.price.Asset
import com.pingo.alertacrypto.domain.price.PriceSource
import com.pingo.alertacrypto.domain.price.PriceTick
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException
import java.math.BigDecimal
import java.time.Instant

class CoinGeckoPriceSource(
    private val api: Api,
) : PriceSource {
    override suspend fun fetchLatest(symbols: Set<String>): List<PriceTick> =
        withContext(Dispatchers.IO) {
            val assets = symbols.map { symbol ->
                Asset.fromSymbol(symbol)
                    ?: throw IllegalArgumentException("Unsupported asset symbol: $symbol")
            }
            require(assets.isNotEmpty()) { "At least one asset symbol is required" }
            val response = api.fetchPrices(
                ids = assets.joinToString(",") { it.coinGeckoId },
                currencies = "brl",
                includeChange = true,
                includeUpdatedAt = true,
            )
            if (response.code() == 429) {
                throw IOException("CoinGecko rate limit reached (HTTP 429)")
            }
            if (!response.isSuccessful) {
                throw IOException("CoinGecko REST returned HTTP ${response.code()}")
            }
            val body = response.body()?.string()
                ?: throw IOException("CoinGecko REST returned an empty response")
            parsePayload(body, assets)
        }

    internal fun parsePayload(payload: String, assets: List<Asset>): List<PriceTick> {
        try {
            val root = JSONObject(payload)
            return assets.map { asset ->
                val quote = root.optJSONObject(asset.coinGeckoId)
                    ?: throw IOException("CoinGecko omitted ${asset.coinGeckoId}")
                val price = quote.optString("brl").toDecimalOrNull()
                    ?: throw IOException("CoinGecko omitted a valid BRL price for ${asset.symbol}")
                val change = quote.optString("brl_24h_change").toDecimalOrNull()
                val updatedAtSeconds = quote.optLong("last_updated_at", 0L)
                if (updatedAtSeconds <= 0L) {
                    throw IOException("CoinGecko omitted a valid timestamp for ${asset.symbol}")
                }
                PriceTick(
                    symbol = asset.symbol,
                    priceBRL = price,
                    change24hPercent = change,
                    observedAt = Instant.ofEpochSecond(updatedAtSeconds),
                )
            }
        } catch (error: JSONException) {
            throw IOException("Invalid CoinGecko response", error)
        }
    }

    private fun String.toDecimalOrNull(): BigDecimal? {
        if (isBlank()) return null
        return try {
            toBigDecimal()
        } catch (_: NumberFormatException) {
            null
        }
    }

    interface Api {
        @GET("api/v3/simple/price")
        suspend fun fetchPrices(
            @Query("ids") ids: String,
            @Query("vs_currencies") currencies: String,
            @Query("include_24hr_change") includeChange: Boolean,
            @Query("include_last_updated_at") includeUpdatedAt: Boolean,
        ): Response<ResponseBody>
    }

    companion object {
        const val BASE_URL = "https://api.coingecko.com/"
    }
}
