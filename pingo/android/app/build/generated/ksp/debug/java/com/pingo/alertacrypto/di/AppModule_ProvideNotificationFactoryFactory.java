package com.pingo.alertacrypto.di;

import android.content.Context;
import com.pingo.alertacrypto.data.notification.AlertNotificationFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppModule_ProvideNotificationFactoryFactory implements Factory<AlertNotificationFactory> {
  private final Provider<Context> contextProvider;

  private AppModule_ProvideNotificationFactoryFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AlertNotificationFactory get() {
    return provideNotificationFactory(contextProvider.get());
  }

  public static AppModule_ProvideNotificationFactoryFactory create(
      Provider<Context> contextProvider) {
    return new AppModule_ProvideNotificationFactoryFactory(contextProvider);
  }

  public static AlertNotificationFactory provideNotificationFactory(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideNotificationFactory(context));
  }
}
