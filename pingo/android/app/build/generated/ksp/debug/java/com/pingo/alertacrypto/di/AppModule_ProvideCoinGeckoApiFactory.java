package com.pingo.alertacrypto.di;

import com.pingo.alertacrypto.data.price.CoinGeckoPriceSource;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;

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
public final class AppModule_ProvideCoinGeckoApiFactory implements Factory<CoinGeckoPriceSource.Api> {
  private final Provider<OkHttpClient> clientProvider;

  private AppModule_ProvideCoinGeckoApiFactory(Provider<OkHttpClient> clientProvider) {
    this.clientProvider = clientProvider;
  }

  @Override
  public CoinGeckoPriceSource.Api get() {
    return provideCoinGeckoApi(clientProvider.get());
  }

  public static AppModule_ProvideCoinGeckoApiFactory create(Provider<OkHttpClient> clientProvider) {
    return new AppModule_ProvideCoinGeckoApiFactory(clientProvider);
  }

  public static CoinGeckoPriceSource.Api provideCoinGeckoApi(OkHttpClient client) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideCoinGeckoApi(client));
  }
}
