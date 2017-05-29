package org.hisp.dhis.android.dataentry.selection;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

public interface SelectionRepository {

    @NonNull
    Flowable<List<SelectionViewModel>> list(@NonNull String uid);

    //Consumer<CharSequence> search();
}
