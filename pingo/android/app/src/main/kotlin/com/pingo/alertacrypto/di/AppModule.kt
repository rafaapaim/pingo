package com.pingo.alertacrypto.di

import com.pingo.alertacrypto.data.price.BinancePriceSource
import com.pingo.alertacrypto.data.price.CoinGeckoPriceSource
import com.pingo.alertacrypto.data.price.PriceRepositoryImpl
import com.pingo.alertacrypto.domain.price.PriceRepository
import com.pingo.alertacrypto.domain.price.PriceSource
import com.pingo.alertacrypto.domain.price.StreamingPriceSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

    @Provides
    @Singleton
    fun provideCoinGeckoApi(client: OkHttpClient): CoinGeckoPriceSource.Api =
        Retrofit.Builder()
            .baseUrl(CoinGeckoPriceSource.BASE_URL)
            .client(client)
            .build()
            .create(CoinGeckoPriceSource.Api::class.java)

    @Provides
    @Singleton
    fun provideBinancePriceSource(client: OkHttpClient): BinancePriceSource =
        BinancePriceSource(client)

    @Provides
    @Singleton
    fun provideCoinGeckoPriceSource(api: CoinGeckoPriceSource.Api): CoinGeckoPriceSource =
        CoinGeckoPriceSource(api)

    @Provides
    @Singleton
    fun providePriceRepository(
        binance: BinancePriceSource,
        coinGecko: CoinGeckoPriceSource,
    ): PriceRepository = PriceRepositoryImpl(binance, coinGecko)
}
