package org.hisp.dhis.android.dataentry.dashboard;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.dagger.PerFragment;

import dagger.Module;

@Module
@PerFragment
public class DashboardModule {

    @NonNull
    private final String enrollmentUid;

    public DashboardModule(@NonNull String enrollmentUid) {
        this.enrollmentUid = enrollmentUid;
    }
}
