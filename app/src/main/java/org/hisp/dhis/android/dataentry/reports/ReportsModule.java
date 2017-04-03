package org.hisp.dhis.android.dataentry.reports;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.dagger.PerActivity;
import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

@PerActivity
@Module
public final class ReportsModule {

    @NonNull
    private final Activity activity;

    @NonNull
    private final String entityUid;

    @NonNull
    private final String entityType;

    @NonNull
    private final String entityName;

    ReportsModule(@NonNull Activity activity, @NonNull String entityUid,
            @NonNull String entityType, @NonNull String entityName) {
        this.activity = activity;
        this.entityUid = entityUid;
        this.entityType = entityType;
        this.entityName = entityName;
    }

    @PerActivity
    @Provides
    ReportsNavigator navigator() {
        switch (entityType) {
            case ReportViewModel.TYPE_TEIS:
                return new TeisNavigatorImpl(activity, entityName);
            case ReportViewModel.TYPE_EVENTS:
                return new SingleEventsNavigatorImpl();
            case ReportViewModel.TYPE_ENROLLMENTS:
                return new EnrollmentsNavigatorImpl();
            default:
                throw new IllegalArgumentException("Unsupported entity type: " + entityType);
        }
    }

    @PerActivity
    @Provides
    ReportsRepository reportsRepository(BriteDatabase briteDatabase) {
        switch (entityType) {
            case ReportViewModel.TYPE_TEIS:
                return new TeisRepositoryImpl(briteDatabase, entityUid);
            case ReportViewModel.TYPE_EVENTS:
                return new SingleEventsRepositoryImpl(briteDatabase, entityUid);
            case ReportViewModel.TYPE_ENROLLMENTS:
                return new EnrollmentsRepositoryImpl(briteDatabase, entityUid,
                        activity.getString(R.string.report_view_program),
                        activity.getString(R.string.report_view_enrollment_status),
                        activity.getString(R.string.report_view_enrollment_date)
                );
            default:
                throw new IllegalArgumentException("Unsupported entity type: " + entityType);
        }
    }

    @PerActivity
    @Provides
    ReportsPresenter reportsPresenter(ReportsRepository reportsRepository,
            SchedulerProvider schedulerProvider) {
        return new ReportsPresenterImpl(schedulerProvider, reportsRepository);
    }
}
