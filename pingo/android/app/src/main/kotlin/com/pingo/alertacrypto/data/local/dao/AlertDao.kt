package com.pingo.alertacrypto.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pingo.alertacrypto.data.local.entity.AlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @Query("SELECT * FROM alerts ORDER BY createdAtEpochMillis DESC")
    fun observeAll(): Flow<List<AlertEntity>>

    @Query("SELECT * FROM alerts WHERE symbol = :symbol AND enabled = 1")
    suspend fun getEnabledForSymbol(symbol: String): List<AlertEntity>

    @Query("SELECT * FROM alerts WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): AlertEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(alert: AlertEntity)

    @Update
    suspend fun update(alert: AlertEntity)

    @Query("UPDATE alerts SET enabled = :enabled, lastPriceBRL = :lastPriceBRL, updatedAtEpochMillis = :updatedAt WHERE id = :id")
    suspend fun setEnabled(id: String, enabled: Boolean, lastPriceBRL: String?, updatedAt: Long)

    @Query("DELETE FROM alerts WHERE id = :id")
    suspend fun deleteById(id: String)
}
