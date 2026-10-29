package com.pingo.alertacrypto.`data`.local.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.pingo.alertacrypto.`data`.local.entity.LatestPriceEntity
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class LatestPriceDao_Impl(
  __db: RoomDatabase,
) : LatestPriceDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfLatestPriceEntity: EntityInsertAdapter<LatestPriceEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfLatestPriceEntity = object : EntityInsertAdapter<LatestPriceEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `latest_prices` (`symbol`,`priceBRL`,`change24hPercent`,`observedAtEpochMillis`,`receivedAtEpochMillis`,`sourceId`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: LatestPriceEntity) {
        statement.bindText(1, entity.symbol)
        statement.bindText(2, entity.priceBRL)
        val _tmpChange24hPercent: String? = entity.change24hPercent
        if (_tmpChange24hPercent == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpChange24hPercent)
        }
        statement.bindLong(4, entity.observedAtEpochMillis)
        statement.bindLong(5, entity.receivedAtEpochMillis)
        statement.bindText(6, entity.sourceId)
      }
    }
  }

  public override suspend fun upsert(price: LatestPriceEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfLatestPriceEntity.insert(_connection, price)
  }

  public override fun observeAll(): Flow<List<LatestPriceEntity>> {
    val _sql: String = "SELECT * FROM latest_prices"
    return createFlow(__db, false, arrayOf("latest_prices")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfSymbol: Int = getColumnIndexOrThrow(_stmt, "symbol")
        val _columnIndexOfPriceBRL: Int = getColumnIndexOrThrow(_stmt, "priceBRL")
        val _columnIndexOfChange24hPercent: Int = getColumnIndexOrThrow(_stmt, "change24hPercent")
        val _columnIndexOfObservedAtEpochMillis: Int = getColumnIndexOrThrow(_stmt, "observedAtEpochMillis")
        val _columnIndexOfReceivedAtEpochMillis: Int = getColumnIndexOrThrow(_stmt, "receivedAtEpochMillis")
        val _columnIndexOfSourceId: Int = getColumnIndexOrThrow(_stmt, "sourceId")
        val _result: MutableList<LatestPriceEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: LatestPriceEntity
          val _tmpSymbol: String
          _tmpSymbol = _stmt.getText(_columnIndexOfSymbol)
          val _tmpPriceBRL: String
          _tmpPriceBRL = _stmt.getText(_columnIndexOfPriceBRL)
          val _tmpChange24hPercent: String?
          if (_stmt.isNull(_columnIndexOfChange24hPercent)) {
            _tmpChange24hPercent = null
          } else {
            _tmpChange24hPercent = _stmt.getText(_columnIndexOfChange24hPercent)
          }
          val _tmpObservedAtEpochMillis: Long
          _tmpObservedAtEpochMillis = _stmt.getLong(_columnIndexOfObservedAtEpochMillis)
          val _tmpReceivedAtEpochMillis: Long
          _tmpReceivedAtEpochMillis = _stmt.getLong(_columnIndexOfReceivedAtEpochMillis)
          val _tmpSourceId: String
          _tmpSourceId = _stmt.getText(_columnIndexOfSourceId)
          _item = LatestPriceEntity(_tmpSymbol,_tmpPriceBRL,_tmpChange24hPercent,_tmpObservedAtEpochMillis,_tmpReceivedAtEpochMillis,_tmpSourceId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAll(): List<LatestPriceEntity> {
    val _sql: String = "SELECT * FROM latest_prices"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfSymbol: Int = getColumnIndexOrThrow(_stmt, "symbol")
        val _columnIndexOfPriceBRL: Int = getColumnIndexOrThrow(_stmt, "priceBRL")
        val _columnIndexOfChange24hPercent: Int = getColumnIndexOrThrow(_stmt, "change24hPercent")
        val _columnIndexOfObservedAtEpochMillis: Int = getColumnIndexOrThrow(_stmt, "observedAtEpochMillis")
        val _columnIndexOfReceivedAtEpochMillis: Int = getColumnIndexOrThrow(_stmt, "receivedAtEpochMillis")
        val _columnIndexOfSourceId: Int = getColumnIndexOrThrow(_stmt, "sourceId")
        val _result: MutableList<LatestPriceEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: LatestPriceEntity
          val _tmpSymbol: String
          _tmpSymbol = _stmt.getText(_columnIndexOfSymbol)
          val _tmpPriceBRL: String
          _tmpPriceBRL = _stmt.getText(_columnIndexOfPriceBRL)
          val _tmpChange24hPercent: String?
          if (_stmt.isNull(_columnIndexOfChange24hPercent)) {
            _tmpChange24hPercent = null
          } else {
            _tmpChange24hPercent = _stmt.getText(_columnIndexOfChange24hPercent)
          }
          val _tmpObservedAtEpochMillis: Long
          _tmpObservedAtEpochMillis = _stmt.getLong(_columnIndexOfObservedAtEpochMillis)
          val _tmpReceivedAtEpochMillis: Long
          _tmpReceivedAtEpochMillis = _stmt.getLong(_columnIndexOfReceivedAtEpochMillis)
          val _tmpSourceId: String
          _tmpSourceId = _stmt.getText(_columnIndexOfSourceId)
          _item = LatestPriceEntity(_tmpSymbol,_tmpPriceBRL,_tmpChange24hPercent,_tmpObservedAtEpochMillis,_tmpReceivedAtEpochMillis,_tmpSourceId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getBySymbol(symbol: String): LatestPriceEntity? {
    val _sql: String = "SELECT * FROM latest_prices WHERE symbol = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, symbol)
        val _columnIndexOfSymbol: Int = getColumnIndexOrThrow(_stmt, "symbol")
        val _columnIndexOfPriceBRL: Int = getColumnIndexOrThrow(_stmt, "priceBRL")
        val _columnIndexOfChange24hPercent: Int = getColumnIndexOrThrow(_stmt, "change24hPercent")
        val _columnIndexOfObservedAtEpochMillis: Int = getColumnIndexOrThrow(_stmt, "observedAtEpochMillis")
        val _columnIndexOfReceivedAtEpochMillis: Int = getColumnIndexOrThrow(_stmt, "receivedAtEpochMillis")
        val _columnIndexOfSourceId: Int = getColumnIndexOrThrow(_stmt, "sourceId")
        val _result: LatestPriceEntity?
        if (_stmt.step()) {
          val _tmpSymbol: String
          _tmpSymbol = _stmt.getText(_columnIndexOfSymbol)
          val _tmpPriceBRL: String
          _tmpPriceBRL = _stmt.getText(_columnIndexOfPriceBRL)
          val _tmpChange24hPercent: String?
          if (_stmt.isNull(_columnIndexOfChange24hPercent)) {
            _tmpChange24hPercent = null
          } else {
            _tmpChange24hPercent = _stmt.getText(_columnIndexOfChange24hPercent)
          }
          val _tmpObservedAtEpochMillis: Long
          _tmpObservedAtEpochMillis = _stmt.getLong(_columnIndexOfObservedAtEpochMillis)
          val _tmpReceivedAtEpochMillis: Long
          _tmpReceivedAtEpochMillis = _stmt.getLong(_columnIndexOfReceivedAtEpochMillis)
          val _tmpSourceId: String
          _tmpSourceId = _stmt.getText(_columnIndexOfSourceId)
          _result = LatestPriceEntity(_tmpSymbol,_tmpPriceBRL,_tmpChange24hPercent,_tmpObservedAtEpochMillis,_tmpReceivedAtEpochMillis,_tmpSourceId)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun delete(symbol: String) {
    val _sql: String = "DELETE FROM latest_prices WHERE symbol = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, symbol)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
