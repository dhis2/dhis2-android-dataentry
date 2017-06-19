package org.hisp.dhis.android.dataentry.form;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

public interface FormRepository {

    /**
     * @param uid corresponds to an event or an enrollment depending on the Repository implementation
     * @return Flowable emitting the title for the FormView
     **/
    @NonNull
    Flowable<String> title(@NonNull String uid);

    @NonNull
    Flowable<String> reportDate(@NonNull String uid);

    @NonNull
    Flowable<ReportStatus> reportStatus(@NonNull String uid);

    /**
     * @param uid corresponds to an event or an enrollment depending on the Repository implementation
     * @return Flowable emitting the sections to be shown in the FormView
     **/
    @NonNull
    Flowable<List<FormSectionViewModel>> sections(@NonNull String uid);

    @NonNull
    Consumer<String> storeReportDate(@NonNull String uid);

    @NonNull
    Consumer<ReportStatus> storeReportStatus(@NonNull String uid);
}