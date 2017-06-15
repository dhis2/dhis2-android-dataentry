package org.hisp.dhis.android.dataentry.dashboard;

import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.dataentry.commons.dagger.PerFragment;
import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

@Module
@PerFragment
public class DashboardModule {

    @NonNull
    private final String enrollmentUid;

    public DashboardModule(@NonNull String enrollmentUid) {
        this.enrollmentUid = enrollmentUid;
    }

    @Provides
    @PerFragment
    DashboardPresenter dashboardPresenter(@NonNull SchedulerProvider schedulerProvider,
                                          @NonNull DashboardRepository dashboardRepository) {
        return new DashboardPresenterImpl(enrollmentUid, schedulerProvider, dashboardRepository);
    }

    @Provides
    @PerFragment
    DashboardRepository dashboardRepository(@NonNull BriteDatabase briteDatabase) {
        return new DashboardRepositoryImpl(briteDatabase);
    }
}
