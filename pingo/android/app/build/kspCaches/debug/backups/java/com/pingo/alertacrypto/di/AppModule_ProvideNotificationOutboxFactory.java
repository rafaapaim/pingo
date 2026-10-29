package com.pingo.alertacrypto.di;

import com.pingo.alertacrypto.data.local.dao.AlertNotificationEventDao;
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
public final class AppModule_ProvideNotificationOutboxFactory implements Factory<RoomAlertNotificationOutbox> {
  private final Provider<AlertNotificationEventDao> daoProvider;

  private AppModule_ProvideNotificationOutboxFactory(
      Provider<AlertNotificationEventDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public RoomAlertNotificationOutbox get() {
    return provideNotificationOutbox(daoProvider.get());
  }

  public static AppModule_ProvideNotificationOutboxFactory create(
      Provider<AlertNotificationEventDao> daoProvider) {
    return new AppModule_ProvideNotificationOutboxFactory(daoProvider);
  }

  public static RoomAlertNotificationOutbox provideNotificationOutbox(
      AlertNotificationEventDao dao) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideNotificationOutbox(dao));
  }
}
