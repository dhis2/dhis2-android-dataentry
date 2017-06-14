package org.hisp.dhis.android.dataentry.form;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.ui.View;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

interface FormView extends View {

    @NonNull
    Observable<ReportStatus> eventStatusChanged();

    @NonNull
    Observable<String> reportDateChanged();

    @NonNull
    Consumer<List<FormSectionViewModel>> renderSectionViewModels();

    @NonNull
    Consumer<String> renderReportDate();

    @NonNull
    Consumer<String> renderTitle();

    @NonNull
    Consumer<ReportStatus> renderStatus();

    @NonNull
    Consumer<String> finishEnrollment();

    void renderStatusChangeSnackBar(@NonNull ReportStatus eventStatus);
}