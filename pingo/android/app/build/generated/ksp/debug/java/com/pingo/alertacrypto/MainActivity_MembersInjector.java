package com.pingo.alertacrypto;

import com.pingo.alertacrypto.data.monitoring.MonitoringController;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<MonitoringController> monitoringControllerProvider;

  private MainActivity_MembersInjector(
      Provider<MonitoringController> monitoringControllerProvider) {
    this.monitoringControllerProvider = monitoringControllerProvider;
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectMonitoringController(instance, monitoringControllerProvider.get());
  }

  public static MembersInjector<MainActivity> create(
      Provider<MonitoringController> monitoringControllerProvider) {
    return new MainActivity_MembersInjector(monitoringControllerProvider);
  }

  @InjectedFieldSignature("com.pingo.alertacrypto.MainActivity.monitoringController")
  public static void injectMonitoringController(MainActivity instance,
      MonitoringController monitoringController) {
    instance.monitoringController = monitoringController;
  }
}
