package org.hisp.dhis.android.dataentry.reports;

import android.app.Activity;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.create.CreateItemsActivity;
import org.hisp.dhis.android.dataentry.create.CreateItemsArgument;
import org.hisp.dhis.android.dataentry.dashboard.DashboardActivity;

import timber.log.Timber;

final class EnrollmentsNavigatorImpl implements ReportsNavigator {

    @NonNull
    private final Activity currentActivity;

    public EnrollmentsNavigatorImpl(@NonNull Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    @Override
    public void navigateTo(@NonNull String enrollmentUid) {
        Timber.d("navigateTo(): %s", enrollmentUid);
        currentActivity.startActivity(DashboardActivity
                .create(currentActivity, enrollmentUid));
    }

    @Override
    public void createFor(@NonNull String trackedEntityInstanceUid) {
        Timber.d("createFor(): %s", trackedEntityInstanceUid);
        //TODO: find a way to put a transatable heading in here: New Event or just the Program name ?..etc
        currentActivity.startActivity(CreateItemsActivity.createIntent(currentActivity,
                CreateItemsArgument.create("New Enrollment ", trackedEntityInstanceUid, CreateItemsArgument.Type
                        .ENROLLMENT)));
    }
}
