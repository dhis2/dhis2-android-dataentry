package org.hisp.dhis.android.dataentry.selection;

import android.support.annotation.NonNull;

import io.reactivex.Observable;

interface SelectionHandler {

    @NonNull
    Observable<SelectionViewModel> viewModelProcessor(@NonNull SelectionViewModel viewModel);
}
