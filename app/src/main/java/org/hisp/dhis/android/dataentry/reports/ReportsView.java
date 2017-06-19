package org.hisp.dhis.android.dataentry.reports;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import org.hisp.dhis.android.dataentry.commons.ui.View;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

interface ReportsView extends View {

    @NonNull
    @UiThread
    Consumer<List<ReportViewModel>> renderReportViewModels();

    @NonNull
    @UiThread
    Observable<Object> createReportsActions();

    @NonNull
    @UiThread
    Consumer<String> createReport();
}
