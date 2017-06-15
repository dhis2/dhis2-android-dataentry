package org.hisp.dhis.android.dataentry.selection;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.commons.ui.View;

import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import rx.exceptions.OnErrorNotImplementedException;

public final class SelectionPresenterImpl implements SelectionPresenter {

    public static final int DEBOUNCE_TIME = 300;
    @NonNull
    private final SelectionArgument arg;

    @NonNull
    private final SelectionRepository repository;

    @NonNull
    private final CompositeDisposable compositeDisposable;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    public SelectionPresenterImpl(@NonNull SelectionArgument arg, @NonNull SelectionRepository repository,
                                  @NonNull SchedulerProvider schedulerProvider) {
        this.arg = arg;
        this.repository = repository;
        this.schedulerProvider = schedulerProvider;
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onAttach(@NonNull View view) {
        if (view instanceof SelectionView) {
            SelectionView selectionView = (SelectionView) view;
            selectionView.setTitle(arg.name());

            compositeDisposable.add(selectionView.subscribeToSearchView()
                    .toFlowable(BackpressureStrategy.LATEST)
                    .subscribeOn(schedulerProvider.ui())
                    .debounce(DEBOUNCE_TIME, TimeUnit.MILLISECONDS, schedulerProvider.computation())
                    .observeOn(schedulerProvider.io())
                    .switchMap(query -> repository.list()
                            .take(1)
                            .flatMap(Flowable::fromIterable)
                            .filter(item -> item.name().contains(query.queryText().toString()))
                            .toList()
                            .toFlowable()
                    )
                    .observeOn(schedulerProvider.ui())
                    .subscribe(selectionView::updateList,
                            throwable -> {
                                throw new OnErrorNotImplementedException(throwable);
                            }
                    )
            );
        }
    }

    @Override
    public void onDetach() {
        compositeDisposable.clear();
    }
}
