package com.pingo.alertacrypto.di;

import com.pingo.alertacrypto.data.local.dao.AlertDao;
import com.pingo.alertacrypto.data.local.dao.LatestPriceDao;
import com.pingo.alertacrypto.domain.alert.AlertRepository;
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
public final class DatabaseModule_ProvideAlertRepositoryFactory implements Factory<AlertRepository> {
  private final Provider<AlertDao> alertDaoProvider;

  private final Provider<LatestPriceDao> latestPriceDaoProvider;

  private DatabaseModule_ProvideAlertRepositoryFactory(Provider<AlertDao> alertDaoProvider,
      Provider<LatestPriceDao> latestPriceDaoProvider) {
    this.alertDaoProvider = alertDaoProvider;
    this.latestPriceDaoProvider = latestPriceDaoProvider;
  }

  @Override
  public AlertRepository get() {
    return provideAlertRepository(alertDaoProvider.get(), latestPriceDaoProvider.get());
  }

  public static DatabaseModule_ProvideAlertRepositoryFactory create(
      Provider<AlertDao> alertDaoProvider, Provider<LatestPriceDao> latestPriceDaoProvider) {
    return new DatabaseModule_ProvideAlertRepositoryFactory(alertDaoProvider, latestPriceDaoProvider);
  }

  public static AlertRepository provideAlertRepository(AlertDao alertDao,
      LatestPriceDao latestPriceDao) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideAlertRepository(alertDao, latestPriceDao));
  }
}
