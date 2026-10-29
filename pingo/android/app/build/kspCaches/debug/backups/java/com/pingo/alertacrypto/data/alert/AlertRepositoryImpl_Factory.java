package com.pingo.alertacrypto.data.alert;

import com.pingo.alertacrypto.data.local.dao.AlertDao;
import com.pingo.alertacrypto.data.local.dao.LatestPriceDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class AlertRepositoryImpl_Factory implements Factory<AlertRepositoryImpl> {
  private final Provider<AlertDao> alertDaoProvider;

  private final Provider<LatestPriceDao> latestPriceDaoProvider;

  private AlertRepositoryImpl_Factory(Provider<AlertDao> alertDaoProvider,
      Provider<LatestPriceDao> latestPriceDaoProvider) {
    this.alertDaoProvider = alertDaoProvider;
    this.latestPriceDaoProvider = latestPriceDaoProvider;
  }

  @Override
  public AlertRepositoryImpl get() {
    return newInstance(alertDaoProvider.get(), latestPriceDaoProvider.get());
  }

  public static AlertRepositoryImpl_Factory create(Provider<AlertDao> alertDaoProvider,
      Provider<LatestPriceDao> latestPriceDaoProvider) {
    return new AlertRepositoryImpl_Factory(alertDaoProvider, latestPriceDaoProvider);
  }

  public static AlertRepositoryImpl newInstance(AlertDao alertDao, LatestPriceDao latestPriceDao) {
    return new AlertRepositoryImpl(alertDao, latestPriceDao);
  }
}
