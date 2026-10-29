package com.pingo.alertacrypto.`data`.local

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.pingo.alertacrypto.`data`.local.dao.AlertDao
import com.pingo.alertacrypto.`data`.local.dao.AlertDao_Impl
import com.pingo.alertacrypto.`data`.local.dao.AlertNotificationEventDao
import com.pingo.alertacrypto.`data`.local.dao.AlertNotificationEventDao_Impl
import com.pingo.alertacrypto.`data`.local.dao.LatestPriceDao
import com.pingo.alertacrypto.`data`.local.dao.LatestPriceDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _alertDao: Lazy<AlertDao> = lazy {
    AlertDao_Impl(this)
  }

  private val _latestPriceDao: Lazy<LatestPriceDao> = lazy {
    LatestPriceDao_Impl(this)
  }

  private val _alertNotificationEventDao: Lazy<AlertNotificationEventDao> = lazy {
    AlertNotificationEventDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1, "e89f5ac91325be7356b4a5fb73135413", "7e1205ed06f0a1985ca6a7ab239ec174") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `alerts` (`id` TEXT NOT NULL, `symbol` TEXT NOT NULL, `targetPriceBRL` TEXT NOT NULL, `direction` TEXT NOT NULL, `repeatMode` TEXT NOT NULL, `armed` INTEGER NOT NULL, `firedUp` INTEGER, `lastPriceBRL` TEXT, `enabled` INTEGER NOT NULL, `createdAtEpochMillis` INTEGER NOT NULL, `updatedAtEpochMillis` INTEGER NOT NULL, `lastFiredAtEpochMillis` INTEGER, `lastFiredTickAtEpochMillis` INTEGER, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_alerts_symbol_enabled` ON `alerts` (`symbol`, `enabled`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `latest_prices` (`symbol` TEXT NOT NULL, `priceBRL` TEXT NOT NULL, `change24hPercent` TEXT, `observedAtEpochMillis` INTEGER NOT NULL, `receivedAtEpochMillis` INTEGER NOT NULL, `sourceId` TEXT NOT NULL, PRIMARY KEY(`symbol`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `alert_notification_events` (`eventId` TEXT NOT NULL, `alertId` TEXT NOT NULL, `symbol` TEXT NOT NULL, `directionUp` INTEGER NOT NULL, `targetPriceBRL` TEXT NOT NULL, `observedPriceBRL` TEXT NOT NULL, `tickObservedAtEpochMillis` INTEGER NOT NULL, `deliveryStatus` TEXT NOT NULL, `createdAtEpochMillis` INTEGER NOT NULL, `updatedAtEpochMillis` INTEGER NOT NULL, PRIMARY KEY(`eventId`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_alert_notification_events_alertId_tickObservedAtEpochMillis` ON `alert_notification_events` (`alertId`, `tickObservedAtEpochMillis`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'e89f5ac91325be7356b4a5fb73135413')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `alerts`")
        connection.execSQL("DROP TABLE IF EXISTS `latest_prices`")
        connection.execSQL("DROP TABLE IF EXISTS `alert_notification_events`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsAlerts: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsAlerts.put("id", TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAlerts.put("symbol", TableInfo.Column("symbol", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAlerts.put("targetPriceBRL", TableInfo.Column("targetPriceBRL", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAlerts.put("direction", TableInfo.Column("direction", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAlerts.put("repeatMode", TableInfo.Column("repeatMode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAlerts.put("armed", TableInfo.Column("armed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAlerts.put("firedUp", TableInfo.Column("firedUp", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAlerts.put("lastPriceBRL", TableInfo.Column("lastPriceBRL", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAlerts.put("enabled", TableInfo.Column("enabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAlerts.put("createdAtEpochMillis", TableInfo.Column("createdAtEpochMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAlerts.put("updatedAtEpochMillis", TableInfo.Column("updatedAtEpochMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAlerts.put("lastFiredAtEpochMillis", TableInfo.Column("lastFiredAtEpochMillis", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAlerts.put("lastFiredTickAtEpochMillis", TableInfo.Column("lastFiredTickAtEpochMillis", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAlerts: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesAlerts: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesAlerts.add(TableInfo.Index("index_alerts_symbol_enabled", false, listOf("symbol", "enabled"), listOf("ASC", "ASC")))
        val _infoAlerts: TableInfo = TableInfo("alerts", _columnsAlerts, _foreignKeysAlerts, _indicesAlerts)
        val _existingAlerts: TableInfo = read(connection, "alerts")
        if (!_infoAlerts.equals(_existingAlerts)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |alerts(com.pingo.alertacrypto.data.local.entity.AlertEntity).
              | Expected:
              |""".trimMargin() + _infoAlerts + """
              |
              | Found:
              |""".trimMargin() + _existingAlerts)
        }
        val _columnsLatestPrices: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsLatestPrices.put("symbol", TableInfo.Column("symbol", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLatestPrices.put("priceBRL", TableInfo.Column("priceBRL", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLatestPrices.put("change24hPercent", TableInfo.Column("change24hPercent", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLatestPrices.put("observedAtEpochMillis", TableInfo.Column("observedAtEpochMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLatestPrices.put("receivedAtEpochMillis", TableInfo.Column("receivedAtEpochMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLatestPrices.put("sourceId", TableInfo.Column("sourceId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysLatestPrices: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesLatestPrices: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoLatestPrices: TableInfo = TableInfo("latest_prices", _columnsLatestPrices, _foreignKeysLatestPrices, _indicesLatestPrices)
        val _existingLatestPrices: TableInfo = read(connection, "latest_prices")
        if (!_infoLatestPrices.equals(_existingLatestPrices)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |latest_prices(com.pingo.alertacrypto.data.local.entity.LatestPriceEntity).
              | Expected:
              |""".trimMargin() + _infoLatestPrices + """
              |
              | Found:
              |""".trimMargin() + _existingLatestPrices)
        }
        val _columnsAlertNotificationEvents: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsAlertNotificationEvents.put("eventId", TableInfo.Column("eventId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAlertNotificationEvents.put("alertId", TableInfo.Column("alertId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAlertNotificationEvents.put("symbol", TableInfo.Column("symbol", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAlertNotificationEvents.put("directionUp", TableInfo.Column("directionUp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAlertNotificationEvents.put("targetPriceBRL", TableInfo.Column("targetPriceBRL", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAlertNotificationEvents.put("observedPriceBRL", TableInfo.Column("observedPriceBRL", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAlertNotificationEvents.put("tickObservedAtEpochMillis", TableInfo.Column("tickObservedAtEpochMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAlertNotificationEvents.put("deliveryStatus", TableInfo.Column("deliveryStatus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAlertNotificationEvents.put("createdAtEpochMillis", TableInfo.Column("createdAtEpochMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAlertNotificationEvents.put("updatedAtEpochMillis", TableInfo.Column("updatedAtEpochMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAlertNotificationEvents: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesAlertNotificationEvents: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesAlertNotificationEvents.add(TableInfo.Index("index_alert_notification_events_alertId_tickObservedAtEpochMillis", true, listOf("alertId", "tickObservedAtEpochMillis"), listOf("ASC", "ASC")))
        val _infoAlertNotificationEvents: TableInfo = TableInfo("alert_notification_events", _columnsAlertNotificationEvents, _foreignKeysAlertNotificationEvents, _indicesAlertNotificationEvents)
        val _existingAlertNotificationEvents: TableInfo = read(connection, "alert_notification_events")
        if (!_infoAlertNotificationEvents.equals(_existingAlertNotificationEvents)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |alert_notification_events(com.pingo.alertacrypto.data.local.entity.AlertNotificationEventEntity).
              | Expected:
              |""".trimMargin() + _infoAlertNotificationEvents + """
              |
              | Found:
              |""".trimMargin() + _existingAlertNotificationEvents)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "alerts", "latest_prices", "alert_notification_events")
  }

  public override fun clearAllTables() {
    super.performClear(false, "alerts", "latest_prices", "alert_notification_events")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(AlertDao::class, AlertDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(LatestPriceDao::class, LatestPriceDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(AlertNotificationEventDao::class, AlertNotificationEventDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun alertDao(): AlertDao = _alertDao.value

  public override fun latestPriceDao(): LatestPriceDao = _latestPriceDao.value

  public override fun alertNotificationEventDao(): AlertNotificationEventDao = _alertNotificationEventDao.value
}
