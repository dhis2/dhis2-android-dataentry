package org.hisp.dhis.android.dataentry.reports.search;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import org.hisp.dhis.android.dataentry.commons.ui.View;
import org.hisp.dhis.android.dataentry.reports.ReportViewModel;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

interface SearchView extends View {

    @NonNull
    Observable<CharSequence> searchBoxActions();

    @NonNull
    @UiThread
    Observable<Object> createReportsActions();

    @NonNull
    @UiThread
    Consumer<List<ReportViewModel>> renderSearchResults();

    @NonNull
    @UiThread
    Consumer<Boolean> renderCreateButton();

    @NonNull
    @UiThread
    Consumer<String> createReport();
}
