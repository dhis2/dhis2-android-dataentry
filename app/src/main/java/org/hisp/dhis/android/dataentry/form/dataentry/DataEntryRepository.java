package org.hisp.dhis.android.dataentry.form.dataentry;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.dataentry.form.dataentry.fields.FieldViewModel;

import java.util.List;

import io.reactivex.Flowable;

public interface DataEntryRepository {

    @NonNull
    Flowable<Long> save(@NonNull String uid, @Nullable String value);

    @NonNull
    Flowable<List<FieldViewModel>> list();
}
