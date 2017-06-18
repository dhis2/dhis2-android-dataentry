package org.hisp.dhis.android.dataentry.selection;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Flowable;

interface SelectionRepository {

    @NonNull
    Flowable<List<SelectionViewModel>> search(@NonNull String query);
}
