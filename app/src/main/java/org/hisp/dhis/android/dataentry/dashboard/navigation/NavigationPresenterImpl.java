package org.hisp.dhis.android.dataentry.dashboard.navigation;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.commons.tuples.Pair;

import io.reactivex.BackpressureStrategy;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.exceptions.OnErrorNotImplementedException;
import timber.log.Timber;

final class NavigationPresenterImpl implements NavigationPresenter {

    @NonNull
    private final String enrollmentUid;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final NavigationRepository navigationRepository;

    @NonNull
    private final CompositeDisposable compositeDisposable;

    NavigationPresenterImpl(@NonNull String enrollmentUid,
                            @NonNull SchedulerProvider schedulerProvider,
                            @NonNull NavigationRepository navigationRepository) {
        this.enrollmentUid = enrollmentUid;
        this.schedulerProvider = schedulerProvider;
        this.navigationRepository = navigationRepository;
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onAttach(@NonNull NavigationView view) {
        compositeDisposable.add(navigationRepository
                .attributes(enrollmentUid)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .map(strings -> {
                    String firstAttribute = strings.size() > 0 ? strings.get(0) : "";
                    String secondAttribute = strings.size() > 1 ? strings.get(1) : "";
                    return Pair.create(firstAttribute, secondAttribute);
                })
                .subscribe(view.renderAttributes(), Timber::e));

        compositeDisposable.add(navigationRepository
                .events(enrollmentUid)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(view.renderEvents(), throwable -> {
                    throw new OnErrorNotImplementedException(throwable);
                }));

        compositeDisposable.add(view.createEventActions()
                .toFlowable(BackpressureStrategy.LATEST)
                .subscribeOn(schedulerProvider.ui())
                .observeOn(schedulerProvider.io())
                .switchMap(click -> navigationRepository.program(enrollmentUid))
                .observeOn(schedulerProvider.ui())
                .subscribe(view.navigateToCreateScreen(), throwable -> {
                    throw new OnErrorNotImplementedException(throwable);
                }));
    }

    @Override
    public void onDetach() {
        compositeDisposable.clear();
    }
}