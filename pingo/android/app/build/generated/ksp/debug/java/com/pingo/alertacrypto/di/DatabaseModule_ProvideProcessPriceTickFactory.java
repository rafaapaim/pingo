package com.pingo.alertacrypto.di;

import com.pingo.alertacrypto.data.alert.PriceTickProcessor;
import com.pingo.alertacrypto.domain.alert.ProcessPriceTick;
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
public final class DatabaseModule_ProvideProcessPriceTickFactory implements Factory<ProcessPriceTick> {
  private final Provider<PriceTickProcessor> processorProvider;

  private DatabaseModule_ProvideProcessPriceTickFactory(
      Provider<PriceTickProcessor> processorProvider) {
    this.processorProvider = processorProvider;
  }

  @Override
  public ProcessPriceTick get() {
    return provideProcessPriceTick(processorProvider.get());
  }

  public static DatabaseModule_ProvideProcessPriceTickFactory create(
      Provider<PriceTickProcessor> processorProvider) {
    return new DatabaseModule_ProvideProcessPriceTickFactory(processorProvider);
  }

  public static ProcessPriceTick provideProcessPriceTick(PriceTickProcessor processor) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideProcessPriceTick(processor));
  }
}
