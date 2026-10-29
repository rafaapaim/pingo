package com.pingo.alertacrypto;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.pingo.alertacrypto.data.alert.PriceTickProcessor;
import com.pingo.alertacrypto.data.local.AppDatabase;
import com.pingo.alertacrypto.data.local.dao.AlertDao;
import com.pingo.alertacrypto.data.local.dao.AlertNotificationEventDao;
import com.pingo.alertacrypto.data.local.dao.LatestPriceDao;
import com.pingo.alertacrypto.data.monitoring.MonitoringController;
import com.pingo.alertacrypto.data.monitoring.MonitoringScheduler;
import com.pingo.alertacrypto.data.monitoring.MonitoringStateStore;
import com.pingo.alertacrypto.data.notification.AlertNotificationDispatcher;
import com.pingo.alertacrypto.data.notification.AlertNotificationFactory;
import com.pingo.alertacrypto.data.notification.RoomAlertNotificationOutbox;
import com.pingo.alertacrypto.data.price.BinancePriceSource;
import com.pingo.alertacrypto.data.price.CoinGeckoPriceSource;
import com.pingo.alertacrypto.di.AppModule_ProvideBinancePriceSourceFactory;
import com.pingo.alertacrypto.di.AppModule_ProvideCoinGeckoApiFactory;
import com.pingo.alertacrypto.di.AppModule_ProvideCoinGeckoPriceSourceFactory;
import com.pingo.alertacrypto.di.AppModule_ProvideNotificationDispatcherFactory;
import com.pingo.alertacrypto.di.AppModule_ProvideNotificationFactoryFactory;
import com.pingo.alertacrypto.di.AppModule_ProvideOkHttpClientFactory;
import com.pingo.alertacrypto.di.AppModule_ProvidePriceRepositoryFactory;
import com.pingo.alertacrypto.di.DatabaseModule_ProvideAlertDaoFactory;
import com.pingo.alertacrypto.di.DatabaseModule_ProvideAlertNotificationEventDaoFactory;
import com.pingo.alertacrypto.di.DatabaseModule_ProvideAlertRepositoryFactory;
import com.pingo.alertacrypto.di.DatabaseModule_ProvideAppDatabaseFactory;
import com.pingo.alertacrypto.di.DatabaseModule_ProvideLatestPriceDaoFactory;
import com.pingo.alertacrypto.di.DatabaseModule_ProvideProcessPriceTickFactory;
import com.pingo.alertacrypto.domain.alert.AlertRepository;
import com.pingo.alertacrypto.domain.alert.AlertUseCases;
import com.pingo.alertacrypto.domain.alert.ProcessPriceTick;
import com.pingo.alertacrypto.domain.price.PriceRepository;
import com.pingo.alertacrypto.presentation.alerts.AlertListViewModel;
import com.pingo.alertacrypto.presentation.alerts.AlertListViewModel_HiltModules;
import com.pingo.alertacrypto.presentation.alerts.AlertListViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.pingo.alertacrypto.presentation.alerts.AlertListViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.pingo.alertacrypto.presentation.prices.PriceListViewModel;
import com.pingo.alertacrypto.presentation.prices.PriceListViewModel_HiltModules;
import com.pingo.alertacrypto.presentation.prices.PriceListViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.pingo.alertacrypto.presentation.prices.PriceListViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;

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
public final class DaggerPingoApplication_HiltComponents_SingletonC {
  private DaggerPingoApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public PingoApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements PingoApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public PingoApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements PingoApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public PingoApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements PingoApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public PingoApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements PingoApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public PingoApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements PingoApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public PingoApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements PingoApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public PingoApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements PingoApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public PingoApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends PingoApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends PingoApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    FragmentCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends PingoApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends PingoApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    ActivityCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    MonitoringController monitoringController() {
      return new MonitoringController(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.scheduler(), singletonCImpl.stateStore());
    }

    Map keySetMapOfClassOfObjectAndBooleanBuilder() {
      MapBuilder mapBuilder = MapBuilder.<String, Boolean>newMapBuilder(2);
      mapBuilder.put(AlertListViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, AlertListViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(PriceListViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, PriceListViewModel_HiltModules.KeyModule.provide());
      return mapBuilder.build();
    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
      injectMainActivity2(mainActivity);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(keySetMapOfClassOfObjectAndBooleanBuilder());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectMonitoringController(instance, monitoringController());
      return instance;
    }
  }

  private static final class ViewModelCImpl extends PingoApplication_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    Provider<AlertListViewModel> alertListViewModelProvider;

    Provider<PriceListViewModel> priceListViewModelProvider;

    ViewModelCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        SavedStateHandle savedStateHandleParam, ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    AlertUseCases alertUseCases() {
      return new AlertUseCases(singletonCImpl.provideAlertRepositoryProvider.get());
    }

    Map hiltViewModelMapMapOfClassOfObjectAndProviderOfViewModelBuilder() {
      MapBuilder mapBuilder = MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(2);
      mapBuilder.put(AlertListViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (alertListViewModelProvider)));
      mapBuilder.put(PriceListViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (priceListViewModelProvider)));
      return mapBuilder.build();
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.alertListViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.priceListViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(hiltViewModelMapMapOfClassOfObjectAndProviderOfViewModelBuilder());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // com.pingo.alertacrypto.presentation.alerts.AlertListViewModel
          return (T) new AlertListViewModel(viewModelCImpl.alertUseCases());

          case 1: // com.pingo.alertacrypto.presentation.prices.PriceListViewModel
          return (T) new PriceListViewModel(singletonCImpl.providePriceRepositoryProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.latestPriceDao(), singletonCImpl.priceTickProcessorProvider.get(), singletonCImpl.provideNotificationDispatcherProvider.get(), singletonCImpl.stateStore());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends PingoApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends PingoApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends PingoApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    Provider<AppDatabase> provideAppDatabaseProvider;

    Provider<PriceTickProcessor> priceTickProcessorProvider;

    Provider<ProcessPriceTick> provideProcessPriceTickProvider;

    Provider<OkHttpClient> provideOkHttpClientProvider;

    Provider<BinancePriceSource> provideBinancePriceSourceProvider;

    Provider<CoinGeckoPriceSource.Api> provideCoinGeckoApiProvider;

    Provider<CoinGeckoPriceSource> provideCoinGeckoPriceSourceProvider;

    Provider<AlertNotificationFactory> provideNotificationFactoryProvider;

    Provider<AlertNotificationDispatcher> provideNotificationDispatcherProvider;

    Provider<AlertRepository> provideAlertRepositoryProvider;

    Provider<PriceRepository> providePriceRepositoryProvider;

    SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    AlertNotificationEventDao alertNotificationEventDao() {
      return DatabaseModule_ProvideAlertNotificationEventDaoFactory.provideAlertNotificationEventDao(provideAppDatabaseProvider.get());
    }

    RoomAlertNotificationOutbox roomAlertNotificationOutbox() {
      return new RoomAlertNotificationOutbox(alertNotificationEventDao());
    }

    AlertDao alertDao() {
      return DatabaseModule_ProvideAlertDaoFactory.provideAlertDao(provideAppDatabaseProvider.get());
    }

    LatestPriceDao latestPriceDao() {
      return DatabaseModule_ProvideLatestPriceDaoFactory.provideLatestPriceDao(provideAppDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideAppDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 2));
      this.priceTickProcessorProvider = DoubleCheck.provider(new SwitchingProvider<PriceTickProcessor>(singletonCImpl, 1));
      this.provideProcessPriceTickProvider = DoubleCheck.provider(new SwitchingProvider<ProcessPriceTick>(singletonCImpl, 0));
      this.provideOkHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 4));
      this.provideBinancePriceSourceProvider = DoubleCheck.provider(new SwitchingProvider<BinancePriceSource>(singletonCImpl, 3));
      this.provideCoinGeckoApiProvider = DoubleCheck.provider(new SwitchingProvider<CoinGeckoPriceSource.Api>(singletonCImpl, 6));
      this.provideCoinGeckoPriceSourceProvider = DoubleCheck.provider(new SwitchingProvider<CoinGeckoPriceSource>(singletonCImpl, 5));
      this.provideNotificationFactoryProvider = DoubleCheck.provider(new SwitchingProvider<AlertNotificationFactory>(singletonCImpl, 8));
      this.provideNotificationDispatcherProvider = DoubleCheck.provider(new SwitchingProvider<AlertNotificationDispatcher>(singletonCImpl, 7));
      this.provideAlertRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<AlertRepository>(singletonCImpl, 9));
      this.providePriceRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<PriceRepository>(singletonCImpl, 10));
    }

    @Override
    public void injectPingoApplication(PingoApplication pingoApplication) {
    }

    @Override
    public MonitoringStateStore stateStore() {
      return new MonitoringStateStore(ApplicationContextModule_ProvideContextFactory.provideContext(applicationContextModule));
    }

    @Override
    public MonitoringScheduler scheduler() {
      return new MonitoringScheduler(ApplicationContextModule_ProvideContextFactory.provideContext(applicationContextModule), stateStore());
    }

    @Override
    public ProcessPriceTick processor() {
      return provideProcessPriceTickProvider.get();
    }

    @Override
    public BinancePriceSource binancePriceSource() {
      return provideBinancePriceSourceProvider.get();
    }

    @Override
    public CoinGeckoPriceSource coinGeckoPriceSource() {
      return provideCoinGeckoPriceSourceProvider.get();
    }

    @Override
    public AlertNotificationDispatcher dispatcher() {
      return provideNotificationDispatcherProvider.get();
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // com.pingo.alertacrypto.domain.alert.ProcessPriceTick
          return (T) DatabaseModule_ProvideProcessPriceTickFactory.provideProcessPriceTick(singletonCImpl.priceTickProcessorProvider.get());

          case 1: // com.pingo.alertacrypto.data.alert.PriceTickProcessor
          return (T) new PriceTickProcessor(singletonCImpl.provideAppDatabaseProvider.get());

          case 2: // com.pingo.alertacrypto.data.local.AppDatabase
          return (T) DatabaseModule_ProvideAppDatabaseFactory.provideAppDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // com.pingo.alertacrypto.data.price.BinancePriceSource
          return (T) AppModule_ProvideBinancePriceSourceFactory.provideBinancePriceSource(singletonCImpl.provideOkHttpClientProvider.get());

          case 4: // okhttp3.OkHttpClient
          return (T) AppModule_ProvideOkHttpClientFactory.provideOkHttpClient();

          case 5: // com.pingo.alertacrypto.data.price.CoinGeckoPriceSource
          return (T) AppModule_ProvideCoinGeckoPriceSourceFactory.provideCoinGeckoPriceSource(singletonCImpl.provideCoinGeckoApiProvider.get());

          case 6: // com.pingo.alertacrypto.data.price.CoinGeckoPriceSource.Api
          return (T) AppModule_ProvideCoinGeckoApiFactory.provideCoinGeckoApi(singletonCImpl.provideOkHttpClientProvider.get());

          case 7: // com.pingo.alertacrypto.data.notification.AlertNotificationDispatcher
          return (T) AppModule_ProvideNotificationDispatcherFactory.provideNotificationDispatcher(singletonCImpl.roomAlertNotificationOutbox(), singletonCImpl.provideNotificationFactoryProvider.get());

          case 8: // com.pingo.alertacrypto.data.notification.AlertNotificationFactory
          return (T) AppModule_ProvideNotificationFactoryFactory.provideNotificationFactory(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 9: // com.pingo.alertacrypto.domain.alert.AlertRepository
          return (T) DatabaseModule_ProvideAlertRepositoryFactory.provideAlertRepository(singletonCImpl.alertDao(), singletonCImpl.latestPriceDao());

          case 10: // com.pingo.alertacrypto.domain.price.PriceRepository
          return (T) AppModule_ProvidePriceRepositoryFactory.providePriceRepository(singletonCImpl.provideBinancePriceSourceProvider.get(), singletonCImpl.provideCoinGeckoPriceSourceProvider.get(), singletonCImpl.provideProcessPriceTickProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
