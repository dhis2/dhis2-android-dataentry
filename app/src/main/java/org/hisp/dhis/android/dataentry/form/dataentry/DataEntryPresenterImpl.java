package org.hisp.dhis.android.dataentry.form.dataentry;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.commons.ui.View;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.FieldViewModel;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import rx.exceptions.OnErrorNotImplementedException;

final class DataEntryPresenterImpl implements DataEntryPresenter {

    @NonNull
    private final DataEntryRepository dataEntryRepository;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final CompositeDisposable disposable;

    @Nullable
    private DataEntryView dataEntryView;

    DataEntryPresenterImpl(@NonNull DataEntryRepository dataEntryRepository,
            @NonNull SchedulerProvider schedulerProvider) {
        this.dataEntryRepository = dataEntryRepository;
        this.schedulerProvider = schedulerProvider;
        this.disposable = new CompositeDisposable();
    }

    @Override
    public void onAttach(@NonNull View view) {
        if (view instanceof DataEntryView) {
            dataEntryView = (DataEntryView) view;
        }
    }

    @Override
    public void showFields(@NonNull String uid) {
        disposable.add(dataEntryRepository.list(uid)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(showFields(), throwable -> {
                    throw new OnErrorNotImplementedException(throwable);
                }));
    }

    @Override
    public void onDetach() {
        disposable.clear();
        dataEntryView = null;
    }

    @NonNull
    private Consumer<List<FieldViewModel>> showFields() {
        return fields -> {
            if (dataEntryView != null) {
                dataEntryView.showFields().accept(fields);
            }
        };
    }
}
