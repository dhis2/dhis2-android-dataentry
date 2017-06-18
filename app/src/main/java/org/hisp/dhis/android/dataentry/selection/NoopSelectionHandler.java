package org.hisp.dhis.android.dataentry.selection;

import android.support.annotation.NonNull;

import io.reactivex.Observable;

final class NoopSelectionHandler implements SelectionHandler {

    @NonNull
    @Override
    public Observable<SelectionViewModel> viewModelProcessor(
            @NonNull SelectionViewModel viewModel) {
        return Observable.just(viewModel);
    }
}
