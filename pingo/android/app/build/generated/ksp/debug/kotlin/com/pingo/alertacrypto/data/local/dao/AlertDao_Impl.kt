package com.pingo.alertacrypto.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.pingo.alertacrypto.`data`.local.entity.AlertEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
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
public class AlertDao_Impl(
  __db: RoomDatabase,
) : AlertDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfAlertEntity: EntityInsertAdapter<AlertEntity>

  private val __updateAdapterOfAlertEntity: EntityDeleteOrUpdateAdapter<AlertEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfAlertEntity = object : EntityInsertAdapter<AlertEntity>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `alerts` (`id`,`symbol`,`targetPriceBRL`,`direction`,`repeatMode`,`armed`,`firedUp`,`lastPriceBRL`,`enabled`,`createdAtEpochMillis`,`updatedAtEpochMillis`,`lastFiredAtEpochMillis`,`lastFiredTickAtEpochMillis`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: AlertEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.symbol)
        statement.bindText(3, entity.targetPriceBRL)
        statement.bindText(4, entity.direction)
        statement.bindText(5, entity.repeatMode)
        val _tmp: Int = if (entity.armed) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        val _tmpFiredUp: Boolean? = entity.firedUp
        val _tmp_1: Int? = _tmpFiredUp?.let { if (it) 1 else 0 }
        if (_tmp_1 == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmp_1.toLong())
        }
        val _tmpLastPriceBRL: String? = entity.lastPriceBRL
        if (_tmpLastPriceBRL == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpLastPriceBRL)
        }
        val _tmp_2: Int = if (entity.enabled) 1 else 0
        statement.bindLong(9, _tmp_2.toLong())
        statement.bindLong(10, entity.createdAtEpochMillis)
        statement.bindLong(11, entity.updatedAtEpochMillis)
        val _tmpLastFiredAtEpochMillis: Long? = entity.lastFiredAtEpochMillis
        if (_tmpLastFiredAtEpochMillis == null) {
          statement.bindNull(12)
        } else {
          statement.bindLong(12, _tmpLastFiredAtEpochMillis)
        }
        val _tmpLastFiredTickAtEpochMillis: Long? = entity.lastFiredTickAtEpochMillis
        if (_tmpLastFiredTickAtEpochMillis == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpLastFiredTickAtEpochMillis)
        }
      }
    }
    this.__updateAdapterOfAlertEntity = object : EntityDeleteOrUpdateAdapter<AlertEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `alerts` SET `id` = ?,`symbol` = ?,`targetPriceBRL` = ?,`direction` = ?,`repeatMode` = ?,`armed` = ?,`firedUp` = ?,`lastPriceBRL` = ?,`enabled` = ?,`createdAtEpochMillis` = ?,`updatedAtEpochMillis` = ?,`lastFiredAtEpochMillis` = ?,`lastFiredTickAtEpochMillis` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: AlertEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.symbol)
        statement.bindText(3, entity.targetPriceBRL)
        statement.bindText(4, entity.direction)
        statement.bindText(5, entity.repeatMode)
        val _tmp: Int = if (entity.armed) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        val _tmpFiredUp: Boolean? = entity.firedUp
        val _tmp_1: Int? = _tmpFiredUp?.let { if (it) 1 else 0 }
        if (_tmp_1 == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmp_1.toLong())
        }
        val _tmpLastPriceBRL: String? = entity.lastPriceBRL
        if (_tmpLastPriceBRL == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpLastPriceBRL)
        }
        val _tmp_2: Int = if (entity.enabled) 1 else 0
        statement.bindLong(9, _tmp_2.toLong())
        statement.bindLong(10, entity.createdAtEpochMillis)
        statement.bindLong(11, entity.updatedAtEpochMillis)
        val _tmpLastFiredAtEpochMillis: Long? = entity.lastFiredAtEpochMillis
        if (_tmpLastFiredAtEpochMillis == null) {
          statement.bindNull(12)
        } else {
          statement.bindLong(12, _tmpLastFiredAtEpochMillis)
        }
        val _tmpLastFiredTickAtEpochMillis: Long? = entity.lastFiredTickAtEpochMillis
        if (_tmpLastFiredTickAtEpochMillis == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpLastFiredTickAtEpochMillis)
        }
        statement.bindText(14, entity.id)
      }
    }
  }

  public override suspend fun insert(alert: AlertEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfAlertEntity.insert(_connection, alert)
  }

  public override suspend fun update(alert: AlertEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfAlertEntity.handle(_connection, alert)
  }

  public override fun observeAll(): Flow<List<AlertEntity>> {
    val _sql: String = "SELECT * FROM alerts ORDER BY createdAtEpochMillis DESC"
    return createFlow(__db, false, arrayOf("alerts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSymbol: Int = getColumnIndexOrThrow(_stmt, "symbol")
        val _columnIndexOfTargetPriceBRL: Int = getColumnIndexOrThrow(_stmt, "targetPriceBRL")
        val _columnIndexOfDirection: Int = getColumnIndexOrThrow(_stmt, "direction")
        val _columnIndexOfRepeatMode: Int = getColumnIndexOrThrow(_stmt, "repeatMode")
        val _columnIndexOfArmed: Int = getColumnIndexOrThrow(_stmt, "armed")
        val _columnIndexOfFiredUp: Int = getColumnIndexOrThrow(_stmt, "firedUp")
        val _columnIndexOfLastPriceBRL: Int = getColumnIndexOrThrow(_stmt, "lastPriceBRL")
        val _columnIndexOfEnabled: Int = getColumnIndexOrThrow(_stmt, "enabled")
        val _columnIndexOfCreatedAtEpochMillis: Int = getColumnIndexOrThrow(_stmt, "createdAtEpochMillis")
        val _columnIndexOfUpdatedAtEpochMillis: Int = getColumnIndexOrThrow(_stmt, "updatedAtEpochMillis")
        val _columnIndexOfLastFiredAtEpochMillis: Int = getColumnIndexOrThrow(_stmt, "lastFiredAtEpochMillis")
        val _columnIndexOfLastFiredTickAtEpochMillis: Int = getColumnIndexOrThrow(_stmt, "lastFiredTickAtEpochMillis")
        val _result: MutableList<AlertEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AlertEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpSymbol: String
          _tmpSymbol = _stmt.getText(_columnIndexOfSymbol)
          val _tmpTargetPriceBRL: String
          _tmpTargetPriceBRL = _stmt.getText(_columnIndexOfTargetPriceBRL)
          val _tmpDirection: String
          _tmpDirection = _stmt.getText(_columnIndexOfDirection)
          val _tmpRepeatMode: String
          _tmpRepeatMode = _stmt.getText(_columnIndexOfRepeatMode)
          val _tmpArmed: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfArmed).toInt()
          _tmpArmed = _tmp != 0
          val _tmpFiredUp: Boolean?
          val _tmp_1: Int?
          if (_stmt.isNull(_columnIndexOfFiredUp)) {
            _tmp_1 = null
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfFiredUp).toInt()
          }
          _tmpFiredUp = _tmp_1?.let { it != 0 }
          val _tmpLastPriceBRL: String?
          if (_stmt.isNull(_columnIndexOfLastPriceBRL)) {
            _tmpLastPriceBRL = null
          } else {
            _tmpLastPriceBRL = _stmt.getText(_columnIndexOfLastPriceBRL)
          }
          val _tmpEnabled: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfEnabled).toInt()
          _tmpEnabled = _tmp_2 != 0
          val _tmpCreatedAtEpochMillis: Long
          _tmpCreatedAtEpochMillis = _stmt.getLong(_columnIndexOfCreatedAtEpochMillis)
          val _tmpUpdatedAtEpochMillis: Long
          _tmpUpdatedAtEpochMillis = _stmt.getLong(_columnIndexOfUpdatedAtEpochMillis)
          val _tmpLastFiredAtEpochMillis: Long?
          if (_stmt.isNull(_columnIndexOfLastFiredAtEpochMillis)) {
            _tmpLastFiredAtEpochMillis = null
          } else {
            _tmpLastFiredAtEpochMillis = _stmt.getLong(_columnIndexOfLastFiredAtEpochMillis)
          }
          val _tmpLastFiredTickAtEpochMillis: Long?
          if (_stmt.isNull(_columnIndexOfLastFiredTickAtEpochMillis)) {
            _tmpLastFiredTickAtEpochMillis = null
          } else {
            _tmpLastFiredTickAtEpochMillis = _stmt.getLong(_columnIndexOfLastFiredTickAtEpochMillis)
          }
          _item = AlertEntity(_tmpId,_tmpSymbol,_tmpTargetPriceBRL,_tmpDirection,_tmpRepeatMode,_tmpArmed,_tmpFiredUp,_tmpLastPriceBRL,_tmpEnabled,_tmpCreatedAtEpochMillis,_tmpUpdatedAtEpochMillis,_tmpLastFiredAtEpochMillis,_tmpLastFiredTickAtEpochMillis)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getEnabledForSymbol(symbol: String): List<AlertEntity> {
    val _sql: String = "SELECT * FROM alerts WHERE symbol = ? AND enabled = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, symbol)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSymbol: Int = getColumnIndexOrThrow(_stmt, "symbol")
        val _columnIndexOfTargetPriceBRL: Int = getColumnIndexOrThrow(_stmt, "targetPriceBRL")
        val _columnIndexOfDirection: Int = getColumnIndexOrThrow(_stmt, "direction")
        val _columnIndexOfRepeatMode: Int = getColumnIndexOrThrow(_stmt, "repeatMode")
        val _columnIndexOfArmed: Int = getColumnIndexOrThrow(_stmt, "armed")
        val _columnIndexOfFiredUp: Int = getColumnIndexOrThrow(_stmt, "firedUp")
        val _columnIndexOfLastPriceBRL: Int = getColumnIndexOrThrow(_stmt, "lastPriceBRL")
        val _columnIndexOfEnabled: Int = getColumnIndexOrThrow(_stmt, "enabled")
        val _columnIndexOfCreatedAtEpochMillis: Int = getColumnIndexOrThrow(_stmt, "createdAtEpochMillis")
        val _columnIndexOfUpdatedAtEpochMillis: Int = getColumnIndexOrThrow(_stmt, "updatedAtEpochMillis")
        val _columnIndexOfLastFiredAtEpochMillis: Int = getColumnIndexOrThrow(_stmt, "lastFiredAtEpochMillis")
        val _columnIndexOfLastFiredTickAtEpochMillis: Int = getColumnIndexOrThrow(_stmt, "lastFiredTickAtEpochMillis")
        val _result: MutableList<AlertEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AlertEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpSymbol: String
          _tmpSymbol = _stmt.getText(_columnIndexOfSymbol)
          val _tmpTargetPriceBRL: String
          _tmpTargetPriceBRL = _stmt.getText(_columnIndexOfTargetPriceBRL)
          val _tmpDirection: String
          _tmpDirection = _stmt.getText(_columnIndexOfDirection)
          val _tmpRepeatMode: String
          _tmpRepeatMode = _stmt.getText(_columnIndexOfRepeatMode)
          val _tmpArmed: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfArmed).toInt()
          _tmpArmed = _tmp != 0
          val _tmpFiredUp: Boolean?
          val _tmp_1: Int?
          if (_stmt.isNull(_columnIndexOfFiredUp)) {
            _tmp_1 = null
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfFiredUp).toInt()
          }
          _tmpFiredUp = _tmp_1?.let { it != 0 }
          val _tmpLastPriceBRL: String?
          if (_stmt.isNull(_columnIndexOfLastPriceBRL)) {
            _tmpLastPriceBRL = null
          } else {
            _tmpLastPriceBRL = _stmt.getText(_columnIndexOfLastPriceBRL)
          }
          val _tmpEnabled: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfEnabled).toInt()
          _tmpEnabled = _tmp_2 != 0
          val _tmpCreatedAtEpochMillis: Long
          _tmpCreatedAtEpochMillis = _stmt.getLong(_columnIndexOfCreatedAtEpochMillis)
          val _tmpUpdatedAtEpochMillis: Long
          _tmpUpdatedAtEpochMillis = _stmt.getLong(_columnIndexOfUpdatedAtEpochMillis)
          val _tmpLastFiredAtEpochMillis: Long?
          if (_stmt.isNull(_columnIndexOfLastFiredAtEpochMillis)) {
            _tmpLastFiredAtEpochMillis = null
          } else {
            _tmpLastFiredAtEpochMillis = _stmt.getLong(_columnIndexOfLastFiredAtEpochMillis)
          }
          val _tmpLastFiredTickAtEpochMillis: Long?
          if (_stmt.isNull(_columnIndexOfLastFiredTickAtEpochMillis)) {
            _tmpLastFiredTickAtEpochMillis = null
          } else {
            _tmpLastFiredTickAtEpochMillis = _stmt.getLong(_columnIndexOfLastFiredTickAtEpochMillis)
          }
          _item = AlertEntity(_tmpId,_tmpSymbol,_tmpTargetPriceBRL,_tmpDirection,_tmpRepeatMode,_tmpArmed,_tmpFiredUp,_tmpLastPriceBRL,_tmpEnabled,_tmpCreatedAtEpochMillis,_tmpUpdatedAtEpochMillis,_tmpLastFiredAtEpochMillis,_tmpLastFiredTickAtEpochMillis)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(id: String): AlertEntity? {
    val _sql: String = "SELECT * FROM alerts WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSymbol: Int = getColumnIndexOrThrow(_stmt, "symbol")
        val _columnIndexOfTargetPriceBRL: Int = getColumnIndexOrThrow(_stmt, "targetPriceBRL")
        val _columnIndexOfDirection: Int = getColumnIndexOrThrow(_stmt, "direction")
        val _columnIndexOfRepeatMode: Int = getColumnIndexOrThrow(_stmt, "repeatMode")
        val _columnIndexOfArmed: Int = getColumnIndexOrThrow(_stmt, "armed")
        val _columnIndexOfFiredUp: Int = getColumnIndexOrThrow(_stmt, "firedUp")
        val _columnIndexOfLastPriceBRL: Int = getColumnIndexOrThrow(_stmt, "lastPriceBRL")
        val _columnIndexOfEnabled: Int = getColumnIndexOrThrow(_stmt, "enabled")
        val _columnIndexOfCreatedAtEpochMillis: Int = getColumnIndexOrThrow(_stmt, "createdAtEpochMillis")
        val _columnIndexOfUpdatedAtEpochMillis: Int = getColumnIndexOrThrow(_stmt, "updatedAtEpochMillis")
        val _columnIndexOfLastFiredAtEpochMillis: Int = getColumnIndexOrThrow(_stmt, "lastFiredAtEpochMillis")
        val _columnIndexOfLastFiredTickAtEpochMillis: Int = getColumnIndexOrThrow(_stmt, "lastFiredTickAtEpochMillis")
        val _result: AlertEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpSymbol: String
          _tmpSymbol = _stmt.getText(_columnIndexOfSymbol)
          val _tmpTargetPriceBRL: String
          _tmpTargetPriceBRL = _stmt.getText(_columnIndexOfTargetPriceBRL)
          val _tmpDirection: String
          _tmpDirection = _stmt.getText(_columnIndexOfDirection)
          val _tmpRepeatMode: String
          _tmpRepeatMode = _stmt.getText(_columnIndexOfRepeatMode)
          val _tmpArmed: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfArmed).toInt()
          _tmpArmed = _tmp != 0
          val _tmpFiredUp: Boolean?
          val _tmp_1: Int?
          if (_stmt.isNull(_columnIndexOfFiredUp)) {
            _tmp_1 = null
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfFiredUp).toInt()
          }
          _tmpFiredUp = _tmp_1?.let { it != 0 }
          val _tmpLastPriceBRL: String?
          if (_stmt.isNull(_columnIndexOfLastPriceBRL)) {
            _tmpLastPriceBRL = null
          } else {
            _tmpLastPriceBRL = _stmt.getText(_columnIndexOfLastPriceBRL)
          }
          val _tmpEnabled: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfEnabled).toInt()
          _tmpEnabled = _tmp_2 != 0
          val _tmpCreatedAtEpochMillis: Long
          _tmpCreatedAtEpochMillis = _stmt.getLong(_columnIndexOfCreatedAtEpochMillis)
          val _tmpUpdatedAtEpochMillis: Long
          _tmpUpdatedAtEpochMillis = _stmt.getLong(_columnIndexOfUpdatedAtEpochMillis)
          val _tmpLastFiredAtEpochMillis: Long?
          if (_stmt.isNull(_columnIndexOfLastFiredAtEpochMillis)) {
            _tmpLastFiredAtEpochMillis = null
          } else {
            _tmpLastFiredAtEpochMillis = _stmt.getLong(_columnIndexOfLastFiredAtEpochMillis)
          }
          val _tmpLastFiredTickAtEpochMillis: Long?
          if (_stmt.isNull(_columnIndexOfLastFiredTickAtEpochMillis)) {
            _tmpLastFiredTickAtEpochMillis = null
          } else {
            _tmpLastFiredTickAtEpochMillis = _stmt.getLong(_columnIndexOfLastFiredTickAtEpochMillis)
          }
          _result = AlertEntity(_tmpId,_tmpSymbol,_tmpTargetPriceBRL,_tmpDirection,_tmpRepeatMode,_tmpArmed,_tmpFiredUp,_tmpLastPriceBRL,_tmpEnabled,_tmpCreatedAtEpochMillis,_tmpUpdatedAtEpochMillis,_tmpLastFiredAtEpochMillis,_tmpLastFiredTickAtEpochMillis)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun setEnabled(
    id: String,
    enabled: Boolean,
    lastPriceBRL: String?,
    updatedAt: Long,
  ) {
    val _sql: String = "UPDATE alerts SET enabled = ?, lastPriceBRL = ?, updatedAtEpochMillis = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: Int = if (enabled) 1 else 0
        _stmt.bindLong(_argIndex, _tmp.toLong())
        _argIndex = 2
        if (lastPriceBRL == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, lastPriceBRL)
        }
        _argIndex = 3
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 4
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteById(id: String) {
    val _sql: String = "DELETE FROM alerts WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
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
