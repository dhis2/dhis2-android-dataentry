package org.hisp.dhis.android.dataentry.reports;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Flowable;

interface ReportsRepository {

    @NonNull
    Flowable<List<ReportViewModel>> reports();
}
