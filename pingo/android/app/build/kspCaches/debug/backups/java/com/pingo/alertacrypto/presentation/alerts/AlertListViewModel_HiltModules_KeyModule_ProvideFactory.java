package com.pingo.alertacrypto.presentation.alerts;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.internal.lifecycle.HiltViewModelMap.KeySet")
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
public final class AlertListViewModel_HiltModules_KeyModule_ProvideFactory implements Factory<Boolean> {
  @Override
  public Boolean get() {
    return provide();
  }

  public static AlertListViewModel_HiltModules_KeyModule_ProvideFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static boolean provide() {
    return AlertListViewModel_HiltModules.KeyModule.provide();
  }

  private static final class InstanceHolder {
    static final AlertListViewModel_HiltModules_KeyModule_ProvideFactory INSTANCE = new AlertListViewModel_HiltModules_KeyModule_ProvideFactory();
  }
}
