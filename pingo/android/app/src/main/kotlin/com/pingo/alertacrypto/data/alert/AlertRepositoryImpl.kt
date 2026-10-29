package com.pingo.alertacrypto.data.alert

import com.pingo.alertacrypto.data.local.dao.AlertDao
import com.pingo.alertacrypto.data.local.dao.LatestPriceDao
import com.pingo.alertacrypto.data.local.entity.AlertEntity
import com.pingo.alertacrypto.domain.alert.Alert
import com.pingo.alertacrypto.domain.alert.AlertDirection
import com.pingo.alertacrypto.domain.alert.AlertRepository
import com.pingo.alertacrypto.domain.alert.RepeatMode
import com.pingo.alertacrypto.domain.price.Asset
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlertRepositoryImpl @Inject constructor(
    private val alertDao: AlertDao,
    private val latestPriceDao: LatestPriceDao,
) : AlertRepository {
    override fun observeAll(): Flow<List<Alert>> =
        alertDao.observeAll().map { rows -> rows.map(AlertEntity::toDomain) }

    override suspend fun create(alert: Alert) {
        validate(alert)
        val lastPrice = latestPriceDao.getBySymbol(alert.symbol)?.priceBRL
        alertDao.insert(alert.copy(lastPriceBRL = lastPrice?.let(::BigDecimal)).toEntity())
    }

    override suspend fun update(alert: Alert) {
        validate(alert)
        val previous = alertDao.getById(alert.id.toString())
            ?: throw NoSuchElementException("Alert ${alert.id} does not exist")
        val evaluationChanged = BigDecimal(previous.targetPriceBRL).compareTo(alert.targetPriceBRL) != 0 ||
            previous.symbol != alert.symbol ||
            previous.direction != alert.direction.name ||
            previous.repeatMode != alert.repeatMode.name
        val updated = alert.copy(
            armed = if (evaluationChanged) true else alert.armed,
            firedUp = if (evaluationChanged) null else alert.firedUp,
            lastPriceBRL = if (evaluationChanged) {
                latestPriceDao.getBySymbol(alert.symbol)?.priceBRL?.let(::BigDecimal)
            } else {
                alert.lastPriceBRL
            },
            lastFiredAt = if (evaluationChanged) null else alert.lastFiredAt,
            lastFiredTickAt = if (evaluationChanged) null else alert.lastFiredTickAt,
            updatedAt = Instant.now(),
        )
        alertDao.update(updated.toEntity())
    }

    override suspend fun setEnabled(id: UUID, enabled: Boolean) {
        val alert = alertDao.getById(id.toString())
            ?: throw NoSuchElementException("Alert $id does not exist")
        val lastPrice = if (enabled) latestPriceDao.getBySymbol(alert.symbol)?.priceBRL else null
        alertDao.setEnabled(
            id = id.toString(),
            enabled = enabled,
            lastPriceBRL = lastPrice,
            updatedAt = Instant.now().toEpochMilli(),
        )
    }

    override suspend fun delete(id: UUID) = alertDao.deleteById(id.toString())

    private fun validate(alert: Alert) {
        require(Asset.fromSymbol(alert.symbol)?.symbol == alert.symbol) {
            "Unsupported or non-canonical asset symbol: ${alert.symbol}"
        }
        require(alert.targetPriceBRL > BigDecimal.ZERO) { "Target price must be positive" }
    }
}

private fun AlertEntity.toDomain(): Alert = Alert(
    id = UUID.fromString(id),
    symbol = symbol,
    targetPriceBRL = BigDecimal(targetPriceBRL),
    direction = AlertDirection.valueOf(direction),
    repeatMode = RepeatMode.valueOf(repeatMode),
    armed = armed,
    firedUp = firedUp,
    lastPriceBRL = lastPriceBRL?.let(::BigDecimal),
    enabled = enabled,
    createdAt = Instant.ofEpochMilli(createdAtEpochMillis),
    updatedAt = Instant.ofEpochMilli(updatedAtEpochMillis),
    lastFiredAt = lastFiredAtEpochMillis?.let(Instant::ofEpochMilli),
    lastFiredTickAt = lastFiredTickAtEpochMillis?.let(Instant::ofEpochMilli),
)

private fun Alert.toEntity(): AlertEntity = AlertEntity(
    id = id.toString(),
    symbol = symbol,
    targetPriceBRL = targetPriceBRL.toPlainString(),
    direction = direction.name,
    repeatMode = repeatMode.name,
    armed = armed,
    firedUp = firedUp,
    lastPriceBRL = lastPriceBRL?.toPlainString(),
    enabled = enabled,
    createdAtEpochMillis = createdAt.toEpochMilli(),
    updatedAtEpochMillis = updatedAt.toEpochMilli(),
    lastFiredAtEpochMillis = lastFiredAt?.toEpochMilli(),
    lastFiredTickAtEpochMillis = lastFiredTickAt?.toEpochMilli(),
)
