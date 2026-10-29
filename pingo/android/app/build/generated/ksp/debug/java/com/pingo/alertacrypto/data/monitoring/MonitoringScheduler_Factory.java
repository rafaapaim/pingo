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
public final class MonitoringScheduler_Factory implements Factory<MonitoringScheduler> {
  private final Provider<Context> contextProvider;

  private final Provider<MonitoringStateStore> stateStoreProvider;

  private MonitoringScheduler_Factory(Provider<Context> contextProvider,
      Provider<MonitoringStateStore> stateStoreProvider) {
    this.contextProvider = contextProvider;
    this.stateStoreProvider = stateStoreProvider;
  }

  @Override
  public MonitoringScheduler get() {
    return newInstance(contextProvider.get(), stateStoreProvider.get());
  }

  public static MonitoringScheduler_Factory create(Provider<Context> contextProvider,
      Provider<MonitoringStateStore> stateStoreProvider) {
    return new MonitoringScheduler_Factory(contextProvider, stateStoreProvider);
  }

  public static MonitoringScheduler newInstance(Context context, MonitoringStateStore stateStore) {
    return new MonitoringScheduler(context, stateStore);
  }
}
