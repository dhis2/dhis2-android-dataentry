package org.hisp.dhis.android.dataentry.dashboard.navigation;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.dataentry.commons.dagger.PerFragment;
import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.dashboard.DashboardNavigator;
import org.hisp.dhis.android.dataentry.dashboard.DualPaneDashboardNavigator;
import org.hisp.dhis.android.dataentry.dashboard.SinglePaneDashboardNavigator;

import dagger.Module;
import dagger.Provides;

@Module
@PerFragment
public class NavigationModule {

    @NonNull
    private final FragmentActivity activity;

    @NonNull
    private final String enrollmentUid;

    @NonNull
    private final Boolean twoPaneLayout;

    public NavigationModule(@NonNull FragmentActivity activity, @NonNull String enrollmentUid,
                            @NonNull Boolean twoPaneLayout) {
        this.activity = activity;
        this.enrollmentUid = enrollmentUid;
        this.twoPaneLayout = twoPaneLayout;
    }

    @Provides
    @PerFragment
    NavigationPresenter dashboardPresenter(@NonNull SchedulerProvider schedulerProvider,
                                           @NonNull NavigationRepository navigationRepository) {
        return new NavigationPresenterImpl(enrollmentUid, schedulerProvider, navigationRepository);
    }

    @Provides
    @PerFragment
    NavigationRepository dashboardRepository(@NonNull BriteDatabase briteDatabase) {
        return new NavigationRepositoryImpl(briteDatabase);
    }

    @Provides
    @PerFragment
    DashboardNavigator dashboardNavigator() {
        if (twoPaneLayout) {
            return new DualPaneDashboardNavigator(activity);
        } else {
            return new SinglePaneDashboardNavigator(activity);
        }
    }
}
