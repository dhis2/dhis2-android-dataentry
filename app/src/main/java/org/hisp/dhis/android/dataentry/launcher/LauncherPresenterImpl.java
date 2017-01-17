package org.hisp.dhis.android.dataentry.launcher;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.dataentry.commons.View;
import org.hisp.dhis.android.dataentry.server.ConfigurationRepository;
import org.hisp.dhis.android.dataentry.utils.SchedulerProvider;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

class LauncherPresenterImpl implements LauncherPresenter {

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final CompositeDisposable compositeDisposable;

    @Nullable
    private final ConfigurationRepository configurationRepository;

    @Nullable
    private LauncherView launcherView;

    public LauncherPresenterImpl(@NonNull SchedulerProvider schedulerProvider,
            @Nullable ConfigurationRepository configurationRepository) {
        this.schedulerProvider = schedulerProvider;
        this.configurationRepository = configurationRepository;
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void isUserLoggedIn() {
        /* in case if user repository is null,
        it means that d2 has not been configured */
        if (configurationRepository == null) {
            navigateToLoginView();
            return;
        }
        compositeDisposable.add(configurationRepository.isUserLoggedIn()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe((isUserLoggedIn) -> {
                    if (isUserLoggedIn) {
                        navigateToHomeView();
                    } else {
                        navigateToLoginView();
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

    private void navigateToHomeView() {
        if (launcherView != null) {
            launcherView.navigateToHomeView();
        }
    }

    private void navigateToLoginView() {
        if (launcherView != null) {
            launcherView.navigateToLoginView();
        }
    }
}
