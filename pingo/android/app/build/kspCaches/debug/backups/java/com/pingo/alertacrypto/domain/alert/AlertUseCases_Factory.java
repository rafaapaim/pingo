package com.pingo.alertacrypto.domain.alert;

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
public final class AlertUseCases_Factory implements Factory<AlertUseCases> {
  private final Provider<AlertRepository> repositoryProvider;

  private AlertUseCases_Factory(Provider<AlertRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public AlertUseCases get() {
    return newInstance(repositoryProvider.get());
  }

  public static AlertUseCases_Factory create(Provider<AlertRepository> repositoryProvider) {
    return new AlertUseCases_Factory(repositoryProvider);
  }

  public static AlertUseCases newInstance(AlertRepository repository) {
    return new AlertUseCases(repository);
  }
}
