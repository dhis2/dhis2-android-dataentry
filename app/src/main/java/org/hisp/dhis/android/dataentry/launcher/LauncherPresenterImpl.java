package org.hisp.dhis.android.dataentry.launcher;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.dataentry.commons.ui.View;
import org.hisp.dhis.android.dataentry.server.UserManager;
import org.hisp.dhis.android.dataentry.utils.SchedulerProvider;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

class LauncherPresenterImpl implements LauncherPresenter {

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final CompositeDisposable compositeDisposable;

    @Nullable
    private final UserManager userManager;

    @Nullable
    private LauncherView launcherView;

    public LauncherPresenterImpl(@NonNull SchedulerProvider schedulerProvider,
            @Nullable UserManager userManager) {
        this.schedulerProvider = schedulerProvider;
        this.userManager = userManager;
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void isUserLoggedIn() {
        /* in case if user repository is null, it means that d2 has not been configured yet */
        if (userManager == null) {
            navigateToLoginView();
            return;
        }

        compositeDisposable.add(userManager.isUserLoggedIn()
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
        if (view instanceof LauncherView) {
            launcherView = (LauncherView) view;
        }
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
