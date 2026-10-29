package com.pingo.alertacrypto.presentation.prices;

import android.content.Context;
import com.pingo.alertacrypto.data.alert.PriceTickProcessor;
import com.pingo.alertacrypto.data.local.dao.LatestPriceDao;
import com.pingo.alertacrypto.data.monitoring.MonitoringStateStore;
import com.pingo.alertacrypto.data.notification.AlertNotificationDispatcher;
import com.pingo.alertacrypto.domain.price.PriceRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class PriceListViewModel_Factory implements Factory<PriceListViewModel> {
  private final Provider<PriceRepository> repositoryProvider;

  private final Provider<Context> contextProvider;

  private final Provider<LatestPriceDao> latestPriceDaoProvider;

  private final Provider<PriceTickProcessor> processorProvider;

  private final Provider<AlertNotificationDispatcher> notificationDispatcherProvider;

  private final Provider<MonitoringStateStore> monitoringStateStoreProvider;

  private PriceListViewModel_Factory(Provider<PriceRepository> repositoryProvider,
      Provider<Context> contextProvider, Provider<LatestPriceDao> latestPriceDaoProvider,
      Provider<PriceTickProcessor> processorProvider,
      Provider<AlertNotificationDispatcher> notificationDispatcherProvider,
      Provider<MonitoringStateStore> monitoringStateStoreProvider) {
    this.repositoryProvider = repositoryProvider;
    this.contextProvider = contextProvider;
    this.latestPriceDaoProvider = latestPriceDaoProvider;
    this.processorProvider = processorProvider;
    this.notificationDispatcherProvider = notificationDispatcherProvider;
    this.monitoringStateStoreProvider = monitoringStateStoreProvider;
  }

  @Override
  public PriceListViewModel get() {
    return newInstance(repositoryProvider.get(), contextProvider.get(), latestPriceDaoProvider.get(), processorProvider.get(), notificationDispatcherProvider.get(), monitoringStateStoreProvider.get());
  }

  public static PriceListViewModel_Factory create(Provider<PriceRepository> repositoryProvider,
      Provider<Context> contextProvider, Provider<LatestPriceDao> latestPriceDaoProvider,
      Provider<PriceTickProcessor> processorProvider,
      Provider<AlertNotificationDispatcher> notificationDispatcherProvider,
      Provider<MonitoringStateStore> monitoringStateStoreProvider) {
    return new PriceListViewModel_Factory(repositoryProvider, contextProvider, latestPriceDaoProvider, processorProvider, notificationDispatcherProvider, monitoringStateStoreProvider);
  }

  public static PriceListViewModel newInstance(PriceRepository repository, Context context,
      LatestPriceDao latestPriceDao, PriceTickProcessor processor,
      AlertNotificationDispatcher notificationDispatcher,
      MonitoringStateStore monitoringStateStore) {
    return new PriceListViewModel(repository, context, latestPriceDao, processor, notificationDispatcher, monitoringStateStore);
  }
}
