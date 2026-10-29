package com.pingo.alertacrypto.presentation.prices;

import com.pingo.alertacrypto.domain.price.PriceRepository;
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
public final class PriceListViewModel_Factory implements Factory<PriceListViewModel> {
  private final Provider<PriceRepository> repositoryProvider;

  private PriceListViewModel_Factory(Provider<PriceRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public PriceListViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static PriceListViewModel_Factory create(Provider<PriceRepository> repositoryProvider) {
    return new PriceListViewModel_Factory(repositoryProvider);
  }

  public static PriceListViewModel newInstance(PriceRepository repository) {
    return new PriceListViewModel(repository);
  }
}
