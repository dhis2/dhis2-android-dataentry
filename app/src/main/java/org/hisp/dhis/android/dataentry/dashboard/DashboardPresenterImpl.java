package org.hisp.dhis.android.dataentry.dashboard;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

class DashboardPresenterImpl implements DashboardPresenter {

    private final String enrollmentUid;
    private final SchedulerProvider schedulerProvider;
    private final DashboardRepository dashboardRepository;
    private final CompositeDisposable compositeDisposable;

    public DashboardPresenterImpl(@NonNull String enrollmentUid,
                                  @NonNull SchedulerProvider schedulerProvider,
                                  @NonNull DashboardRepository dashboardRepository) {
        this.enrollmentUid = enrollmentUid;
        this.schedulerProvider = schedulerProvider;
        this.dashboardRepository = dashboardRepository;
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onAttach(@NonNull DashboardView view) {
        compositeDisposable.add(dashboardRepository
                .attributes(enrollmentUid)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(view.renderAttributes(), Timber::e));

        compositeDisposable.add(dashboardRepository
                .events(enrollmentUid)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(view.renderEvents(), Timber::e));
    }

    @Override
    public void onDetach() {
        compositeDisposable.clear();
    }
}