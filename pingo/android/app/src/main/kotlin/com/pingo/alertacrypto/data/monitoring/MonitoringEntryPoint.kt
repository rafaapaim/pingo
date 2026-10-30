package com.pingo.alertacrypto.data.monitoring

import com.pingo.alertacrypto.data.alert.PriceTickProcessor
import com.pingo.alertacrypto.data.notification.AlertNotificationDispatcher
import com.pingo.alertacrypto.data.price.BinancePriceSource
import com.pingo.alertacrypto.data.price.CoinGeckoPriceSource
import com.pingo.alertacrypto.domain.alert.ProcessPriceTick
import com.pingo.alertacrypto.widget.PriceWidgetUpdater
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface MonitoringEntryPoint {
    fun stateStore(): MonitoringStateStore
    fun scheduler(): MonitoringScheduler
    fun processor(): ProcessPriceTick
    fun binancePriceSource(): BinancePriceSource
    fun coinGeckoPriceSource(): CoinGeckoPriceSource
    fun dispatcher(): AlertNotificationDispatcher
    fun priceWidgetUpdater(): PriceWidgetUpdater
}
