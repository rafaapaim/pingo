package com.pingo.alertacrypto.di;

import com.pingo.alertacrypto.data.local.AppDatabase;
import com.pingo.alertacrypto.data.local.dao.LatestPriceDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class DatabaseModule_ProvideLatestPriceDaoFactory implements Factory<LatestPriceDao> {
  private final Provider<AppDatabase> databaseProvider;

  private DatabaseModule_ProvideLatestPriceDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public LatestPriceDao get() {
    return provideLatestPriceDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideLatestPriceDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideLatestPriceDaoFactory(databaseProvider);
  }

  public static LatestPriceDao provideLatestPriceDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideLatestPriceDao(database));
  }
}
