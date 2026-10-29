package com.pingo.alertacrypto.data.price

import com.pingo.alertacrypto.domain.price.Asset
import com.pingo.alertacrypto.domain.price.PriceRepository
import com.pingo.alertacrypto.domain.price.PriceSource
import com.pingo.alertacrypto.domain.price.PriceUpdate
import com.pingo.alertacrypto.domain.price.StreamingPriceSource
import com.pingo.alertacrypto.data.local.dao.LatestPriceDao
import com.pingo.alertacrypto.data.local.entity.LatestPriceEntity
import com.pingo.alertacrypto.data.local.entity.toPriceTick
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
    private val latestPriceDao: LatestPriceDao,
) : PriceRepository {
    override fun observePrices(): Flow<PriceUpdate> = flow {
        val symbols = Asset.supported.map(Asset::symbol).toSet()
        latestPriceDao.getAll()
            .filter { it.symbol in symbols }
            .forEach { emit(PriceUpdate.Tick(it.toPriceTick())) }
        var retryDelayMillis = INITIAL_RETRY_DELAY_MILLIS

        while (true) {
            var streamFailed = false
            emitAll(
                streamingSource.observeTicks(symbols)
                    .map { tick ->
                        persistLatest(tick, "binance_ws")
                        tick
                    }
                    .map { PriceUpdate.Tick(it) }
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
                    persistLatest(tick, "coingecko_rest")
                    emit(PriceUpdate.Tick(tick))
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

    private suspend fun persistLatest(
        tick: PriceTick,
        sourceId: String,
    ) {
        val existing = latestPriceDao.getBySymbol(tick.symbol)
        if (existing != null && existing.observedAtEpochMillis > tick.observedAt.toEpochMilli()) return
        latestPriceDao.upsert(
            LatestPriceEntity(
                symbol = tick.symbol,
                priceBRL = tick.priceBRL.toPlainString(),
                change24hPercent = tick.change24hPercent?.toPlainString(),
                observedAtEpochMillis = tick.observedAt.toEpochMilli(),
                receivedAtEpochMillis = System.currentTimeMillis(),
                sourceId = sourceId,
            ),
        )
    }

    companion object {
        private const val INITIAL_RETRY_DELAY_MILLIS = 1_000L
        private const val MAX_RETRY_DELAY_MILLIS = 60_000L
    }
}
