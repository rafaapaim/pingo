package com.pingo.alertacrypto.di;

import com.pingo.alertacrypto.data.price.CoinGeckoPriceSource;
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
public final class AppModule_ProvideCoinGeckoPriceSourceFactory implements Factory<CoinGeckoPriceSource> {
  private final Provider<CoinGeckoPriceSource.Api> apiProvider;

  private AppModule_ProvideCoinGeckoPriceSourceFactory(
      Provider<CoinGeckoPriceSource.Api> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public CoinGeckoPriceSource get() {
    return provideCoinGeckoPriceSource(apiProvider.get());
  }

  public static AppModule_ProvideCoinGeckoPriceSourceFactory create(
      Provider<CoinGeckoPriceSource.Api> apiProvider) {
    return new AppModule_ProvideCoinGeckoPriceSourceFactory(apiProvider);
  }

  public static CoinGeckoPriceSource provideCoinGeckoPriceSource(CoinGeckoPriceSource.Api api) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideCoinGeckoPriceSource(api));
  }
}
