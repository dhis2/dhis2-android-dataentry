package org.hisp.dhis.android.dataentry.reports.search;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.commons.ui.View;

import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.exceptions.OnErrorNotImplementedException;

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
    public void onAttach(@NonNull View view) {
        if (view instanceof SearchView) {
            SearchView searchView = (SearchView) view;

            compositeDisposable.add(searchView.createReportsActions()
                    .map(action -> searchArguments.entityUid())
                    .subscribe(searchView.createReport(), throwable -> {
                        throw new OnErrorNotImplementedException(throwable);
                    }));

            compositeDisposable.add(searchView.searchBoxActions()
                    .subscribeOn(schedulerProvider.ui())
                    .debounce(256, TimeUnit.MILLISECONDS, schedulerProvider.computation())
                    .distinctUntilChanged()
                    .filter(event -> event.editable() != null)
                    .map(event -> event.editable().toString())
                    .toFlowable(BackpressureStrategy.LATEST)
                    .observeOn(schedulerProvider.io())
                    .switchMap(token -> searchRepository.search(token))
                    .observeOn(schedulerProvider.ui())
                    .subscribe(searchView.renderSearchResults(), throwable -> {
                        throw new OnErrorNotImplementedException(throwable);
                    }));
        }
    }

    @Override
    public void onDetach() {
        compositeDisposable.clear();
    }
}
