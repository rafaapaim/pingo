package com.pingo.alertacrypto.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pingo.alertacrypto.data.local.converter.DecimalConverters
import com.pingo.alertacrypto.data.local.dao.AlertDao
import com.pingo.alertacrypto.data.local.dao.AlertNotificationEventDao
import com.pingo.alertacrypto.data.local.dao.LatestPriceDao
import com.pingo.alertacrypto.data.local.entity.AlertEntity
import com.pingo.alertacrypto.data.local.entity.AlertNotificationEventEntity
import com.pingo.alertacrypto.data.local.entity.LatestPriceEntity

@Database(
    entities = [AlertEntity::class, LatestPriceEntity::class, AlertNotificationEventEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(DecimalConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun alertDao(): AlertDao
    abstract fun latestPriceDao(): LatestPriceDao
    abstract fun alertNotificationEventDao(): AlertNotificationEventDao
}
