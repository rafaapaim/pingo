package com.pingo.alertacrypto.data.notification;

import com.pingo.alertacrypto.data.local.dao.AlertNotificationEventDao;
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
public final class RoomAlertNotificationOutbox_Factory implements Factory<RoomAlertNotificationOutbox> {
  private final Provider<AlertNotificationEventDao> daoProvider;

  private RoomAlertNotificationOutbox_Factory(Provider<AlertNotificationEventDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public RoomAlertNotificationOutbox get() {
    return newInstance(daoProvider.get());
  }

  public static RoomAlertNotificationOutbox_Factory create(
      Provider<AlertNotificationEventDao> daoProvider) {
    return new RoomAlertNotificationOutbox_Factory(daoProvider);
  }

  public static RoomAlertNotificationOutbox newInstance(AlertNotificationEventDao dao) {
    return new RoomAlertNotificationOutbox(dao);
  }
}
