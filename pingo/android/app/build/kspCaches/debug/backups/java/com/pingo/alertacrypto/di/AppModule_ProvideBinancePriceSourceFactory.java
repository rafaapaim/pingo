package com.pingo.alertacrypto.di;

import com.pingo.alertacrypto.data.price.BinancePriceSource;
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
public final class AppModule_ProvideBinancePriceSourceFactory implements Factory<BinancePriceSource> {
  private final Provider<OkHttpClient> clientProvider;

  private AppModule_ProvideBinancePriceSourceFactory(Provider<OkHttpClient> clientProvider) {
    this.clientProvider = clientProvider;
  }

  @Override
  public BinancePriceSource get() {
    return provideBinancePriceSource(clientProvider.get());
  }

  public static AppModule_ProvideBinancePriceSourceFactory create(
      Provider<OkHttpClient> clientProvider) {
    return new AppModule_ProvideBinancePriceSourceFactory(clientProvider);
  }

  public static BinancePriceSource provideBinancePriceSource(OkHttpClient client) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideBinancePriceSource(client));
  }
}
