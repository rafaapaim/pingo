package com.pingo.alertacrypto.presentation.alerts;

import com.pingo.alertacrypto.domain.alert.AlertUseCases;
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
public final class AlertListViewModel_Factory implements Factory<AlertListViewModel> {
  private final Provider<AlertUseCases> useCasesProvider;

  private AlertListViewModel_Factory(Provider<AlertUseCases> useCasesProvider) {
    this.useCasesProvider = useCasesProvider;
  }

  @Override
  public AlertListViewModel get() {
    return newInstance(useCasesProvider.get());
  }

  public static AlertListViewModel_Factory create(Provider<AlertUseCases> useCasesProvider) {
    return new AlertListViewModel_Factory(useCasesProvider);
  }

  public static AlertListViewModel newInstance(AlertUseCases useCases) {
    return new AlertListViewModel(useCases);
  }
}
