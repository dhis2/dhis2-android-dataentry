package org.hisp.dhis.android.dataentry.selection;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.commons.ui.View;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import rx.exceptions.OnErrorNotImplementedException;
import timber.log.Timber;

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
        this.onAttach(view, "");
    }

    @Override
    public void onAttach(@NonNull View view, @NonNull String query) {
        if (view instanceof SelectionView) {
            SelectionView selectionView = (SelectionView) view;
            selectionView.setTitle(arg.name());
            disposable.add(repository.list(arg.uid())
                    .map(list -> search(list, query))
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(((SelectionView) view).update(), err -> {
                        throw new OnErrorNotImplementedException(this.getClass().getName(), err);
                    })
            );
        }
    }

    @Override
    public void onDetach() {
        disposable.clear();
    }

    private List<SelectionViewModel> search(List<SelectionViewModel> list, @NonNull String query) {
        Timber.d("text changed !" + query); //TODO:
        return list;
    }
}
