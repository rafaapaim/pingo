package com.pingo.alertacrypto.`data`.local.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.pingo.alertacrypto.`data`.local.entity.AlertNotificationEventEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AlertNotificationEventDao_Impl(
  __db: RoomDatabase,
) : AlertNotificationEventDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfAlertNotificationEventEntity:
      EntityInsertAdapter<AlertNotificationEventEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfAlertNotificationEventEntity = object : EntityInsertAdapter<AlertNotificationEventEntity>() {
      protected override fun createQuery(): String = "INSERT OR IGNORE INTO `alert_notification_events` (`eventId`,`alertId`,`symbol`,`directionUp`,`targetPriceBRL`,`observedPriceBRL`,`tickObservedAtEpochMillis`,`deliveryStatus`,`createdAtEpochMillis`,`updatedAtEpochMillis`) VALUES (?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: AlertNotificationEventEntity) {
        statement.bindText(1, entity.eventId)
        statement.bindText(2, entity.alertId)
        statement.bindText(3, entity.symbol)
        val _tmp: Int = if (entity.directionUp) 1 else 0
        statement.bindLong(4, _tmp.toLong())
        statement.bindText(5, entity.targetPriceBRL)
        statement.bindText(6, entity.observedPriceBRL)
        statement.bindLong(7, entity.tickObservedAtEpochMillis)
        statement.bindText(8, entity.deliveryStatus)
        statement.bindLong(9, entity.createdAtEpochMillis)
        statement.bindLong(10, entity.updatedAtEpochMillis)
      }
    }
  }

  public override suspend fun insertIfAbsent(event: AlertNotificationEventEntity): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfAlertNotificationEventEntity.insertAndReturnId(_connection, event)
    _result
  }

  public override suspend fun getPending(): List<AlertNotificationEventEntity> {
    val _sql: String = "SELECT * FROM alert_notification_events WHERE deliveryStatus = 'PENDING' ORDER BY createdAtEpochMillis"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfEventId: Int = getColumnIndexOrThrow(_stmt, "eventId")
        val _columnIndexOfAlertId: Int = getColumnIndexOrThrow(_stmt, "alertId")
        val _columnIndexOfSymbol: Int = getColumnIndexOrThrow(_stmt, "symbol")
        val _columnIndexOfDirectionUp: Int = getColumnIndexOrThrow(_stmt, "directionUp")
        val _columnIndexOfTargetPriceBRL: Int = getColumnIndexOrThrow(_stmt, "targetPriceBRL")
        val _columnIndexOfObservedPriceBRL: Int = getColumnIndexOrThrow(_stmt, "observedPriceBRL")
        val _columnIndexOfTickObservedAtEpochMillis: Int = getColumnIndexOrThrow(_stmt, "tickObservedAtEpochMillis")
        val _columnIndexOfDeliveryStatus: Int = getColumnIndexOrThrow(_stmt, "deliveryStatus")
        val _columnIndexOfCreatedAtEpochMillis: Int = getColumnIndexOrThrow(_stmt, "createdAtEpochMillis")
        val _columnIndexOfUpdatedAtEpochMillis: Int = getColumnIndexOrThrow(_stmt, "updatedAtEpochMillis")
        val _result: MutableList<AlertNotificationEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AlertNotificationEventEntity
          val _tmpEventId: String
          _tmpEventId = _stmt.getText(_columnIndexOfEventId)
          val _tmpAlertId: String
          _tmpAlertId = _stmt.getText(_columnIndexOfAlertId)
          val _tmpSymbol: String
          _tmpSymbol = _stmt.getText(_columnIndexOfSymbol)
          val _tmpDirectionUp: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirectionUp).toInt()
          _tmpDirectionUp = _tmp != 0
          val _tmpTargetPriceBRL: String
          _tmpTargetPriceBRL = _stmt.getText(_columnIndexOfTargetPriceBRL)
          val _tmpObservedPriceBRL: String
          _tmpObservedPriceBRL = _stmt.getText(_columnIndexOfObservedPriceBRL)
          val _tmpTickObservedAtEpochMillis: Long
          _tmpTickObservedAtEpochMillis = _stmt.getLong(_columnIndexOfTickObservedAtEpochMillis)
          val _tmpDeliveryStatus: String
          _tmpDeliveryStatus = _stmt.getText(_columnIndexOfDeliveryStatus)
          val _tmpCreatedAtEpochMillis: Long
          _tmpCreatedAtEpochMillis = _stmt.getLong(_columnIndexOfCreatedAtEpochMillis)
          val _tmpUpdatedAtEpochMillis: Long
          _tmpUpdatedAtEpochMillis = _stmt.getLong(_columnIndexOfUpdatedAtEpochMillis)
          _item = AlertNotificationEventEntity(_tmpEventId,_tmpAlertId,_tmpSymbol,_tmpDirectionUp,_tmpTargetPriceBRL,_tmpObservedPriceBRL,_tmpTickObservedAtEpochMillis,_tmpDeliveryStatus,_tmpCreatedAtEpochMillis,_tmpUpdatedAtEpochMillis)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateDeliveryStatus(
    eventId: String,
    status: String,
    updatedAt: Long,
  ) {
    val _sql: String = "UPDATE alert_notification_events SET deliveryStatus = ?, updatedAtEpochMillis = ? WHERE eventId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, eventId)
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
