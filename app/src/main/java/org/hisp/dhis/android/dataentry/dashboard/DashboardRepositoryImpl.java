package org.hisp.dhis.android.dataentry.dashboard;

import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import java.util.List;

import io.reactivex.Flowable;

class DashboardRepositoryImpl implements DashboardRepository {

    @NonNull
    private final BriteDatabase briteDataBase;

    DashboardRepositoryImpl(@NonNull BriteDatabase briteDatabase) {
        this.briteDataBase = briteDatabase;
    }

    @NonNull
    @Override
    public Flowable<List<EventViewModel>> events(@NonNull String enrollmentUid) {
        return null;
    }
}