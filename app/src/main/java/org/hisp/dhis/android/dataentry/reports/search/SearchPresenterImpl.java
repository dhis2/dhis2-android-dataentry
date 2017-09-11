package org.hisp.dhis.android.dataentry.reports.search;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.commons.utils.OnErrorHandler;

import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.exceptions.OnErrorNotImplementedException;
import io.reactivex.observables.ConnectableObservable;

import static org.hisp.dhis.android.dataentry.commons.utils.StringUtils.isEmpty;

final class SearchPresenterImpl implements SearchPresenter {

    @NonNull
    private final SearchArguments searchArguments;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final SearchRepository searchRepository;

    @NonNull
    private final CompositeDisposable compositeDisposable;

    SearchPresenterImpl(@NonNull SearchArguments searchArguments,
            @NonNull SchedulerProvider schedulerProvider,
            @NonNull SearchRepository searchRepository) {
        this.searchArguments = searchArguments;
        this.schedulerProvider = schedulerProvider;
        this.searchRepository = searchRepository;
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onAttach(@NonNull SearchView searchView) {
        compositeDisposable.add(searchView.createReportsActions()
                .map(action -> searchArguments.entityUid())
                .subscribe(searchView.createReport(), throwable -> {
                    throw new OnErrorNotImplementedException(throwable);
                }));

        ConnectableObservable<CharSequence> searchActions =
                searchView.searchBoxActions().publish();

        compositeDisposable.add(searchActions.refCount()
                .subscribeOn(schedulerProvider.ui())
                .observeOn(schedulerProvider.ui())
                .map(action -> !isEmpty(action.toString()))
                .subscribe(searchView.renderCreateButton(), OnErrorHandler.create()));

        compositeDisposable.add(searchActions.refCount()
                .subscribeOn(schedulerProvider.ui())
                .debounce(256, TimeUnit.MILLISECONDS, schedulerProvider.computation())
                .distinctUntilChanged()
                .map(event -> event.toString())
                .toFlowable(BackpressureStrategy.LATEST)
                .observeOn(schedulerProvider.io())
                .switchMap(token -> searchRepository.search(token))
                .observeOn(schedulerProvider.ui())
                .subscribe(searchView.renderSearchResults(), OnErrorHandler.create()));
    }

    @Override
    public void onDetach() {
        compositeDisposable.clear();
    }
}
