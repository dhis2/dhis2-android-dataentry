package org.hisp.dhis.android.dataentry.create;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.dashboard.DashboardActivity;

class EnrollmentNavigator implements CreateItemsNavigator {

    @NonNull
    private final CreateItemsActivity currentActivity;

    EnrollmentNavigator(@NonNull CreateItemsActivity currentActivity) {
        this.currentActivity = currentActivity;
    }

    @Override
    public void navigateTo(@NonNull String uid) {
        currentActivity.startActivity(DashboardActivity.create(currentActivity, uid));
        currentActivity.finish();
    }
}
