package org.hisp.dhis.android.dataentry.selection;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Flowable;

public interface SelectionRepository {

    Flowable<List<SelectionViewModel>> list(@NonNull String uid);
}
