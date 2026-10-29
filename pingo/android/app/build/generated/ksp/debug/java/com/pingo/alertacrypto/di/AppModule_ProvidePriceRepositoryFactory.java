package com.pingo.alertacrypto.di;

import com.pingo.alertacrypto.data.local.dao.LatestPriceDao;
import com.pingo.alertacrypto.data.price.BinancePriceSource;
import com.pingo.alertacrypto.data.price.CoinGeckoPriceSource;
import com.pingo.alertacrypto.domain.price.PriceRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class AppModule_ProvidePriceRepositoryFactory implements Factory<PriceRepository> {
  private final Provider<BinancePriceSource> binanceProvider;

  private final Provider<CoinGeckoPriceSource> coinGeckoProvider;

  private final Provider<LatestPriceDao> latestPriceDaoProvider;

  private AppModule_ProvidePriceRepositoryFactory(Provider<BinancePriceSource> binanceProvider,
      Provider<CoinGeckoPriceSource> coinGeckoProvider,
      Provider<LatestPriceDao> latestPriceDaoProvider) {
    this.binanceProvider = binanceProvider;
    this.coinGeckoProvider = coinGeckoProvider;
    this.latestPriceDaoProvider = latestPriceDaoProvider;
  }

  @Override
  public PriceRepository get() {
    return providePriceRepository(binanceProvider.get(), coinGeckoProvider.get(), latestPriceDaoProvider.get());
  }

  public static AppModule_ProvidePriceRepositoryFactory create(
      Provider<BinancePriceSource> binanceProvider,
      Provider<CoinGeckoPriceSource> coinGeckoProvider,
      Provider<LatestPriceDao> latestPriceDaoProvider) {
    return new AppModule_ProvidePriceRepositoryFactory(binanceProvider, coinGeckoProvider, latestPriceDaoProvider);
  }

  public static PriceRepository providePriceRepository(BinancePriceSource binance,
      CoinGeckoPriceSource coinGecko, LatestPriceDao latestPriceDao) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.providePriceRepository(binance, coinGecko, latestPriceDao));
  }
}
