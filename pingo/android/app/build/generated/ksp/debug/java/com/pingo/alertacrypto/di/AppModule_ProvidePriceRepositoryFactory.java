package com.pingo.alertacrypto.di;

import com.pingo.alertacrypto.data.price.BinancePriceSource;
import com.pingo.alertacrypto.data.price.CoinGeckoPriceSource;
import com.pingo.alertacrypto.domain.alert.ProcessPriceTick;
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

  private final Provider<ProcessPriceTick> processorProvider;

  private AppModule_ProvidePriceRepositoryFactory(Provider<BinancePriceSource> binanceProvider,
      Provider<CoinGeckoPriceSource> coinGeckoProvider,
      Provider<ProcessPriceTick> processorProvider) {
    this.binanceProvider = binanceProvider;
    this.coinGeckoProvider = coinGeckoProvider;
    this.processorProvider = processorProvider;
  }

  @Override
  public PriceRepository get() {
    return providePriceRepository(binanceProvider.get(), coinGeckoProvider.get(), processorProvider.get());
  }

  public static AppModule_ProvidePriceRepositoryFactory create(
      Provider<BinancePriceSource> binanceProvider,
      Provider<CoinGeckoPriceSource> coinGeckoProvider,
      Provider<ProcessPriceTick> processorProvider) {
    return new AppModule_ProvidePriceRepositoryFactory(binanceProvider, coinGeckoProvider, processorProvider);
  }

  public static PriceRepository providePriceRepository(BinancePriceSource binance,
      CoinGeckoPriceSource coinGecko, ProcessPriceTick processor) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.providePriceRepository(binance, coinGecko, processor));
  }
}
