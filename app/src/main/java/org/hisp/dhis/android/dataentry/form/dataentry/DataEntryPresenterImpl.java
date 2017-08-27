package org.hisp.dhis.android.dataentry.form.dataentry;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.form.FormRepository;

import io.reactivex.disposables.CompositeDisposable;
import rx.exceptions.OnErrorNotImplementedException;
import timber.log.Timber;

final class DataEntryPresenterImpl implements DataEntryPresenter {

    @NonNull
    private final DataEntryStore dataEntryStore;

    @NonNull
    private final DataEntryRepository dataEntryRepository;

    @NonNull
    private final FormRepository formRepository;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final CompositeDisposable disposable;

    DataEntryPresenterImpl(@NonNull DataEntryStore dataEntryStore,
            @NonNull DataEntryRepository dataEntryRepository,
            @NonNull FormRepository formRepository,
            @NonNull SchedulerProvider schedulerProvider) {
        this.dataEntryStore = dataEntryStore;
        this.dataEntryRepository = dataEntryRepository;
        this.formRepository = formRepository;
        this.schedulerProvider = schedulerProvider;
        this.disposable = new CompositeDisposable();
    }

    @Override
    public void onAttach(@NonNull DataEntryView dataEntryView) {
        // application of the rule effects is
        // going to happen within this chain

        // ToDo: find a way to make RuleEngine resilient to errors
        // ToDo: i.e. provide meaningful output in case something went wrong
        disposable.add(dataEntryRepository.list()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(dataEntryView.showFields(), throwable -> {
                    throw new OnErrorNotImplementedException(throwable);
                }));

        disposable.add(dataEntryView.rowActions()
                .subscribeOn(schedulerProvider.ui())
                .observeOn(schedulerProvider.io())
                .switchMap(action -> {
                    Timber.d("dataEntryRepository.save(uid=[%s], value=[%s])",
                            action.id(), action.value());
                    return dataEntryStore.save(action.id(), action.value());
                })
                .subscribe(result -> Timber.d(result.toString()), throwable -> {
                    throw new OnErrorNotImplementedException(throwable);
                }));
    }

    @Override
    public void onDetach() {
        disposable.clear();
    }
}
