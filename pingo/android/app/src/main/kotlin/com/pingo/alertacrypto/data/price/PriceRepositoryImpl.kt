package com.pingo.alertacrypto.data.price

import com.pingo.alertacrypto.domain.price.Asset
import com.pingo.alertacrypto.domain.price.PriceRepository
import com.pingo.alertacrypto.domain.price.PriceSource
import com.pingo.alertacrypto.domain.price.PriceUpdate
import com.pingo.alertacrypto.domain.price.StreamingPriceSource
import com.pingo.alertacrypto.domain.alert.ProcessPriceTick
import com.pingo.alertacrypto.domain.price.PriceTick
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import kotlin.math.min

class PriceRepositoryImpl(
    private val streamingSource: StreamingPriceSource,
    private val fallbackSource: PriceSource,
    private val processor: ProcessPriceTick,
) : PriceRepository {
    override fun observePrices(): Flow<PriceUpdate> = flow {
        val symbols = Asset.supported.map(Asset::symbol).toSet()
        var retryDelayMillis = INITIAL_RETRY_DELAY_MILLIS

        while (true) {
            var streamFailed = false
            emitAll(
                streamingSource.observeTicks(symbols)
                    .map { tick ->
                        processor.process(tick, "binance_ws")
                        tick
                    }
                    .map { PriceUpdate.Tick(it, "binance_ws") }
                    .catch { error ->
                        if (error is CancellationException) throw error
                        streamFailed = true
                        emit(PriceUpdate.Unavailable(error.message ?: "Falha na conexão WebSocket"))
                    },
            )
            if (!streamFailed) {
                emit(PriceUpdate.Unavailable("Stream de preços encerrado"))
            }

            try {
                fallbackSource.fetchLatest(symbols).forEach { tick ->
                    processor.process(tick, "coingecko_rest")
                    emit(PriceUpdate.Tick(tick, "coingecko_rest"))
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: IOException) {
                emit(PriceUpdate.Unavailable(error.message ?: "Falha ao consultar a fonte REST"))
            }

            delay(retryDelayMillis)
            retryDelayMillis = min(retryDelayMillis * 2, MAX_RETRY_DELAY_MILLIS)
        }
    }

    companion object {
        private const val INITIAL_RETRY_DELAY_MILLIS = 1_000L
        private const val MAX_RETRY_DELAY_MILLIS = 60_000L
    }
}
