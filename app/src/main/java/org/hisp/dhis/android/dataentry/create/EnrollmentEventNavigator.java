package org.hisp.dhis.android.dataentry.create;

import android.support.annotation.NonNull;

final class EnrollmentEventNavigator implements CreateItemsNavigator {

    @NonNull
    private final CreateItemsActivity currentActivity;

    EnrollmentEventNavigator(@NonNull CreateItemsActivity currentActivity) {
        this.currentActivity = currentActivity;
    }

    @Override
    public void navigateTo(@NonNull String uid) {
        // navigating up to the dashboard activity
        currentActivity.finish();
    }
}
