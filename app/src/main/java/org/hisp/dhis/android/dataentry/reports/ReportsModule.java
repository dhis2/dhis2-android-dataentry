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
    private final ReportsArguments reportsArguments;

    ReportsModule(@NonNull Activity activity, @NonNull ReportsArguments reportsArguments) {
        this.activity = activity;
        this.reportsArguments = reportsArguments;
    }

    @PerActivity
    @Provides
    ReportsNavigator navigator() {
        switch (reportsArguments.entityType()) {
            case ReportsArguments.TYPE_EVENTS:
                return new SingleEventsNavigatorImpl(activity,
                        activity.getString(R.string.create_event));
            case ReportsArguments.TYPE_ENROLLMENTS:
                return new EnrollmentsNavigatorImpl(activity);
            default:
                throw new IllegalArgumentException("Unsupported entity type: "
                        + reportsArguments.entityType());
        }
    }

    @PerActivity
    @Provides
    ReportsRepository reportsRepository(BriteDatabase briteDatabase) {
        switch (reportsArguments.entityType()) {
            case ReportsArguments.TYPE_EVENTS:
                return new SingleEventsRepositoryImpl(briteDatabase);
            case ReportsArguments.TYPE_ENROLLMENTS:
                return new EnrollmentsRepositoryImpl(briteDatabase,
                        activity.getString(R.string.report_view_program),
                        activity.getString(R.string.report_view_enrollment_status),
                        activity.getString(R.string.report_view_enrollment_date));
            default:
                throw new IllegalArgumentException("Unsupported entity type: "
                        + reportsArguments.entityType());
        }
    }

    @PerActivity
    @Provides
    ReportsPresenter reportsPresenter(@NonNull ReportsRepository reportsRepository,
            @NonNull SchedulerProvider schedulerProvider) {
        return new ReportsPresenterImpl(reportsArguments, schedulerProvider, reportsRepository);
    }
}
