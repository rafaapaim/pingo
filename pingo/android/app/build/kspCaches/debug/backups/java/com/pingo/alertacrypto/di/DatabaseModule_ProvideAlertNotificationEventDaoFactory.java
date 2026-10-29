package com.pingo.alertacrypto.di;

import com.pingo.alertacrypto.data.local.AppDatabase;
import com.pingo.alertacrypto.data.local.dao.AlertNotificationEventDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideAlertNotificationEventDaoFactory implements Factory<AlertNotificationEventDao> {
  private final Provider<AppDatabase> databaseProvider;

  private DatabaseModule_ProvideAlertNotificationEventDaoFactory(
      Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public AlertNotificationEventDao get() {
    return provideAlertNotificationEventDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideAlertNotificationEventDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideAlertNotificationEventDaoFactory(databaseProvider);
  }

  public static AlertNotificationEventDao provideAlertNotificationEventDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideAlertNotificationEventDao(database));
  }
}
