package com.pingo.alertacrypto.di

import android.content.Context
import androidx.room.Room
import com.pingo.alertacrypto.data.local.AppDatabase
import com.pingo.alertacrypto.data.local.dao.AlertDao
import com.pingo.alertacrypto.data.local.dao.AlertNotificationEventDao
import com.pingo.alertacrypto.data.local.dao.LatestPriceDao
import com.pingo.alertacrypto.data.alert.AlertRepositoryImpl
import com.pingo.alertacrypto.domain.alert.AlertRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "pingo.db").build()

    @Provides
    fun provideAlertDao(database: AppDatabase): AlertDao = database.alertDao()

    @Provides
    fun provideLatestPriceDao(database: AppDatabase): LatestPriceDao = database.latestPriceDao()

    @Provides
    fun provideAlertNotificationEventDao(database: AppDatabase): AlertNotificationEventDao =
        database.alertNotificationEventDao()

    @Provides
    @Singleton
    fun provideAlertRepository(
        alertDao: AlertDao,
        latestPriceDao: LatestPriceDao,
    ): AlertRepository = AlertRepositoryImpl(alertDao, latestPriceDao)
}
