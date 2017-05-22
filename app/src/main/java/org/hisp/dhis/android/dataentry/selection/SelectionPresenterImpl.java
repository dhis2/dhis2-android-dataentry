package org.hisp.dhis.android.dataentry.selection;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.commons.ui.View;

import io.reactivex.disposables.CompositeDisposable;
import rx.exceptions.OnErrorNotImplementedException;

public final class SelectionPresenterImpl implements SelectionPresenter {

    @NonNull
    private final SelectionArgument arg;

    @NonNull
    private final SelectionRepository repository;

    @NonNull
    private final CompositeDisposable disposable;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    public SelectionPresenterImpl(@NonNull SelectionArgument arg, @NonNull SelectionRepository repository,
                                  @NonNull SchedulerProvider schedulerProvider) {
        this.arg = arg;
        this.repository = repository;
        this.schedulerProvider = schedulerProvider;
        disposable = new CompositeDisposable();
    }

    @Override
    public void onAttach(@NonNull View view) {
        if (view instanceof SelectionView) {
            disposable.add(repository.list(arg.uid())
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(((SelectionView) view).update(arg.name()), err -> {
                        throw new OnErrorNotImplementedException(this.getClass().getName(), err);
                    })
            );
        }
    }

    @Override
    public void onDetach() {
        disposable.clear();
    }
}
