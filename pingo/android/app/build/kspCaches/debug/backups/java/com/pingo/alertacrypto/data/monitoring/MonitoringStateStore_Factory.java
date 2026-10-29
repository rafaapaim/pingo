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
public final class MonitoringStateStore_Factory implements Factory<MonitoringStateStore> {
  private final Provider<Context> contextProvider;

  private MonitoringStateStore_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public MonitoringStateStore get() {
    return newInstance(contextProvider.get());
  }

  public static MonitoringStateStore_Factory create(Provider<Context> contextProvider) {
    return new MonitoringStateStore_Factory(contextProvider);
  }

  public static MonitoringStateStore newInstance(Context context) {
    return new MonitoringStateStore(context);
  }
}
