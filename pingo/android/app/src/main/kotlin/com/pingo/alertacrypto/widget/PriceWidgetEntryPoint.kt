package com.pingo.alertacrypto.widget

import com.pingo.alertacrypto.data.local.dao.LatestPriceDao
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface PriceWidgetEntryPoint {
    fun latestPriceDao(): LatestPriceDao
}
