package com.pingo.alertacrypto.data.monitoring;

import android.content.Context;
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
public final class MonitoringController_Factory implements Factory<MonitoringController> {
  private final Provider<Context> contextProvider;

  private final Provider<MonitoringScheduler> schedulerProvider;

  private final Provider<MonitoringStateStore> stateStoreProvider;

  private MonitoringController_Factory(Provider<Context> contextProvider,
      Provider<MonitoringScheduler> schedulerProvider,
      Provider<MonitoringStateStore> stateStoreProvider) {
    this.contextProvider = contextProvider;
    this.schedulerProvider = schedulerProvider;
    this.stateStoreProvider = stateStoreProvider;
  }

  @Override
  public MonitoringController get() {
    return newInstance(contextProvider.get(), schedulerProvider.get(), stateStoreProvider.get());
  }

  public static MonitoringController_Factory create(Provider<Context> contextProvider,
      Provider<MonitoringScheduler> schedulerProvider,
      Provider<MonitoringStateStore> stateStoreProvider) {
    return new MonitoringController_Factory(contextProvider, schedulerProvider, stateStoreProvider);
  }

  public static MonitoringController newInstance(Context context, MonitoringScheduler scheduler,
      MonitoringStateStore stateStore) {
    return new MonitoringController(context, scheduler, stateStore);
  }
}
