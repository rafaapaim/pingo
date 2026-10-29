package com.pingo.alertacrypto.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pingo.alertacrypto.data.local.entity.LatestPriceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LatestPriceDao {
    @Query("SELECT * FROM latest_prices")
    fun observeAll(): Flow<List<LatestPriceEntity>>

    @Query("SELECT * FROM latest_prices")
    suspend fun getAll(): List<LatestPriceEntity>

    @Query("SELECT * FROM latest_prices WHERE symbol = :symbol LIMIT 1")
    suspend fun getBySymbol(symbol: String): LatestPriceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(price: LatestPriceEntity)

    @Query("DELETE FROM latest_prices WHERE symbol = :symbol")
    suspend fun delete(symbol: String)
}
