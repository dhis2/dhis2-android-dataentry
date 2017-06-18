package org.hisp.dhis.android.dataentry.selection;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;

import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import rx.exceptions.OnErrorNotImplementedException;

final class SelectionPresenterImpl implements SelectionPresenter {
    private static final int DEBOUNCE_TIME = 300;

    @NonNull
    private final String name;

    @NonNull
    private final SelectionRepository repository;

    @NonNull
    private final SelectionHandler selectionHandler;

    @NonNull
    private final CompositeDisposable compositeDisposable;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    SelectionPresenterImpl(@NonNull String name,
            @NonNull SelectionRepository repository,
            @NonNull SelectionHandler selectionHandler,
            @NonNull SchedulerProvider schedulerProvider) {
        this.name = name;
        this.repository = repository;
        this.selectionHandler = selectionHandler;
        this.schedulerProvider = schedulerProvider;
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onAttach(@NonNull SelectionView selectionView) {
        compositeDisposable.add(Observable.just(name)
                .subscribeOn(schedulerProvider.ui())
                .observeOn(schedulerProvider.ui())
                .subscribe(selectionView.renderTitle(), throwable -> {
                    throw new OnErrorNotImplementedException(throwable);
                }));

        compositeDisposable.add(selectionView.searchView()
                .toFlowable(BackpressureStrategy.LATEST)
                .subscribeOn(schedulerProvider.ui())
                .debounce(DEBOUNCE_TIME, TimeUnit.MILLISECONDS, schedulerProvider.computation())
                .observeOn(schedulerProvider.io())
                .switchMap(query -> repository.search(query.queryText().toString()))
                .observeOn(schedulerProvider.ui())
                .subscribe(selectionView.renderSearchResults(), throwable -> {
                    throw new OnErrorNotImplementedException(throwable);
                }));

        compositeDisposable.add(selectionView.searchResultClicks()
                .subscribeOn(schedulerProvider.ui())
                .observeOn(schedulerProvider.io())
                .switchMap(selectionHandler::viewModelProcessor)
                .observeOn(schedulerProvider.ui())
                .subscribe(selectionView.navigateTo(), throwable -> {
                    throw new OnErrorNotImplementedException(throwable);
                }));
    }

    @Override
    public void onDetach() {
        compositeDisposable.clear();
    }
}
