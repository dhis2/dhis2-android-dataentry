package org.hisp.dhis.android.dataentry.launcher;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.dataentry.commons.ConfigurationRepository;
import org.hisp.dhis.android.dataentry.commons.View;
import org.hisp.dhis.android.dataentry.user.UserRepository;
import org.hisp.dhis.android.dataentry.utils.SchedulerProvider;

import io.reactivex.ObservableSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import timber.log.Timber;

import static io.reactivex.Observable.just;
import static org.hisp.dhis.android.dataentry.utils.StringUtils.isEmpty;

public class LauncherPresenterImpl implements LauncherPresenter {

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final ConfigurationRepository configurationRepository;

    @NonNull
    private final CompositeDisposable compositeDisposable;

    @Nullable
    private final UserRepository userRepository;

    @Nullable
    private LauncherView launcherView;

    public LauncherPresenterImpl(@NonNull SchedulerProvider schedulerProvider,
                                 @NonNull ConfigurationRepository configurationRepository,
                                 @Nullable UserRepository userRepository) {
        this.schedulerProvider = schedulerProvider;
        this.configurationRepository = configurationRepository;
        this.userRepository = userRepository;
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void isUserLoggedIn() {
        compositeDisposable.add(configurationRepository.configuration()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .switchMap(new Function<ConfigurationModel, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(ConfigurationModel config) throws Exception {
                        if (isEmpty(config.serverUrl())) {
                            return just(false);
                        }

                        if (userRepository == null) {
                            return just(false);
                        }

                        return userRepository.isUserLoggedIn();
                    }
                })
                .subscribe((isUserLoggedIn) -> {
                    if (launcherView != null) {
                        if (isUserLoggedIn) {
                            launcherView.navigateToHomeView();
                        } else {
                            launcherView.navigateToLoginView();
                        }
                    }
                }, Timber::e));
    }

    @Override
    public void onAttach(@NonNull View view) {
        launcherView = (LauncherView) view;
    }

    @Override
    public void onDetach() {
        compositeDisposable.clear();
        launcherView = null;
    }
}
