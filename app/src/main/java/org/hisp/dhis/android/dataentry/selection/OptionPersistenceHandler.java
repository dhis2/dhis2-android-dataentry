package org.hisp.dhis.android.dataentry.selection;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.form.dataentry.DataEntryStore;

import io.reactivex.Observable;

final class OptionPersistenceHandler implements SelectionHandler {

    @NonNull
    private final DataEntryStore dataEntryStore;

    @NonNull
    private final String fieldUid;

    OptionPersistenceHandler(@NonNull DataEntryStore dataEntryStore,
            @NonNull String fieldUid) {
        this.dataEntryStore = dataEntryStore;
        this.fieldUid = fieldUid;
    }

    @NonNull
    @Override
    public Observable<SelectionViewModel> viewModelProcessor(
            @NonNull SelectionViewModel viewModel) {
        return dataEntryStore.save(fieldUid, viewModel.code())
                .map(id -> viewModel)
                .toObservable();
    }
}
