package org.hisp.dhis.android.dataentry.form.dataentry;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.form.dataentry.fields.FormItemViewModel;

import java.util.List;

import io.reactivex.Flowable;

public interface DataEntryRepository {

    @NonNull
    Flowable<List<FormItemViewModel>> fields();
}
