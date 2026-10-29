package com.pingo.alertacrypto.data.alert;

import com.pingo.alertacrypto.data.local.AppDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class PriceTickProcessor_Factory implements Factory<PriceTickProcessor> {
  private final Provider<AppDatabase> databaseProvider;

  private PriceTickProcessor_Factory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public PriceTickProcessor get() {
    return newInstance(databaseProvider.get());
  }

  public static PriceTickProcessor_Factory create(Provider<AppDatabase> databaseProvider) {
    return new PriceTickProcessor_Factory(databaseProvider);
  }

  public static PriceTickProcessor newInstance(AppDatabase database) {
    return new PriceTickProcessor(database);
  }
}
