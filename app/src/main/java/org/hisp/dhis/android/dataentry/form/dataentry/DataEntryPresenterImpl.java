package org.hisp.dhis.android.dataentry.form.dataentry;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.commons.ui.View;

import io.reactivex.disposables.CompositeDisposable;
import rx.exceptions.OnErrorNotImplementedException;
import timber.log.Timber;

final class DataEntryPresenterImpl implements DataEntryPresenter {

    @NonNull
    private final DataEntryRepository dataEntryRepository;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final CompositeDisposable disposable;

    DataEntryPresenterImpl(@NonNull DataEntryRepository dataEntryRepository,
            @NonNull SchedulerProvider schedulerProvider) {
        this.dataEntryRepository = dataEntryRepository;
        this.schedulerProvider = schedulerProvider;
        this.disposable = new CompositeDisposable();
    }

    @Override
    public void onAttach(@NonNull View view) {
        if (view instanceof DataEntryView) {
            DataEntryView dataEntryView = (DataEntryView) view;
            disposable.add(dataEntryRepository.list()
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(dataEntryView.showFields(), throwable -> {
                        throw new OnErrorNotImplementedException(throwable);
                    }));
            disposable.add(dataEntryView.rowActions()
                    .subscribeOn(schedulerProvider.ui())
                    .observeOn(schedulerProvider.io())
                    .switchMap(action -> dataEntryRepository.save(action.id(), action.value()))
                    .subscribe(action -> Timber.d(action.toString()), throwable -> {
                        throw new OnErrorNotImplementedException(throwable);
                    }));
        }
    }

    @Override
    public void onDetach() {
        disposable.clear();
    }
}
