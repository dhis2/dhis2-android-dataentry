package org.hisp.dhis.android.dataentry.reports.search;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.reports.ReportViewModel;

import java.util.List;

import io.reactivex.Flowable;

interface SearchRepository {
    @NonNull
    Flowable<List<ReportViewModel>> search(@NonNull String token);
}
