package org.hisp.dhis.android.dataentry.form;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.form.dataentry.DataEntryViewArguments;

import java.util.List;

import io.reactivex.Flowable;

public interface FormRepository {

    /**
     * @param uid corresponds to an event or an enrollment depending on the Repository implementation
     * @return Flowable emitting the title for the FormView
     **/
    @NonNull
    Flowable<String> title(@NonNull String uid);

    /**
     * @param uid corresponds to an event or an enrollment depending on the Repository implementation
     * @return Flowable emitting the sections to be shown in the FormView
     **/
    @NonNull
    Flowable<List<DataEntryViewArguments>> sections(@NonNull String uid);

}