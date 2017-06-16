package org.hisp.dhis.android.dataentry.create;

import android.support.annotation.NonNull;

class EnrollmentsNavigator implements CreateItemsNavigator {

    @NonNull
    private final CreateItemsActivity currentActivity;

    public EnrollmentsNavigator(@NonNull CreateItemsActivity currentActivity) {
        this.currentActivity = currentActivity;
    }

    @Override
    public void navigateTo(@NonNull String uid) {
        // ToDo: navigate to the dashboard activity
    }
}
