package com.pingo.alertacrypto.di;

import com.pingo.alertacrypto.data.notification.AlertNotificationDispatcher;
import com.pingo.alertacrypto.data.notification.AlertNotificationFactory;
import com.pingo.alertacrypto.data.notification.RoomAlertNotificationOutbox;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppModule_ProvideNotificationDispatcherFactory implements Factory<AlertNotificationDispatcher> {
  private final Provider<RoomAlertNotificationOutbox> outboxProvider;

  private final Provider<AlertNotificationFactory> publisherProvider;

  private AppModule_ProvideNotificationDispatcherFactory(
      Provider<RoomAlertNotificationOutbox> outboxProvider,
      Provider<AlertNotificationFactory> publisherProvider) {
    this.outboxProvider = outboxProvider;
    this.publisherProvider = publisherProvider;
  }

  @Override
  public AlertNotificationDispatcher get() {
    return provideNotificationDispatcher(outboxProvider.get(), publisherProvider.get());
  }

  public static AppModule_ProvideNotificationDispatcherFactory create(
      Provider<RoomAlertNotificationOutbox> outboxProvider,
      Provider<AlertNotificationFactory> publisherProvider) {
    return new AppModule_ProvideNotificationDispatcherFactory(outboxProvider, publisherProvider);
  }

  public static AlertNotificationDispatcher provideNotificationDispatcher(
      RoomAlertNotificationOutbox outbox, AlertNotificationFactory publisher) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideNotificationDispatcher(outbox, publisher));
  }
}
