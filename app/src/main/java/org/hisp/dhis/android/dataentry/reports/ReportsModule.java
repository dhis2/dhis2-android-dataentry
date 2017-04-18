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
            case ReportsArguments.TYPE_TEIS:
                return new TeisNavigatorImpl(activity, reportsArguments.entityName());
            case ReportsArguments.TYPE_EVENTS:
                return new SingleEventsNavigatorImpl();
            case ReportsArguments.TYPE_ENROLLMENTS:
                return new EnrollmentsNavigatorImpl();
            default:
                throw new IllegalArgumentException("Unsupported entity type: "
                        + reportsArguments.entityType());
        }
    }

    @PerActivity
    @Provides
    ReportsRepository reportsRepository(BriteDatabase briteDatabase) {
        switch (reportsArguments.entityType()) {
            case ReportsArguments.TYPE_TEIS:
                return new TeisRepositoryImpl(briteDatabase, reportsArguments.entityUid());
            case ReportsArguments.TYPE_EVENTS:
                return new SingleEventsRepositoryImpl(briteDatabase, reportsArguments.entityUid());
            case ReportsArguments.TYPE_ENROLLMENTS:
                return new EnrollmentsRepositoryImpl(briteDatabase, reportsArguments.entityUid(),
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
    ReportsPresenter reportsPresenter(ReportsRepository reportsRepository,
            SchedulerProvider schedulerProvider) {
        return new ReportsPresenterImpl(schedulerProvider, reportsRepository);
    }
}
