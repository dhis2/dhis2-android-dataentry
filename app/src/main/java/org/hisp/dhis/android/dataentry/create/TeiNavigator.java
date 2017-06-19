package org.hisp.dhis.android.dataentry.create;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.dashboard.DashboardActivity;
import org.hisp.dhis.android.dataentry.form.FormActivity;
import org.hisp.dhis.android.dataentry.form.FormViewArguments;

final class TeiNavigator implements CreateItemsNavigator {

    @NonNull
    private final CreateItemsActivity currentActivity;

    TeiNavigator(@NonNull CreateItemsActivity activity) {
        this.currentActivity = activity;
    }

    @Override
    public void navigateTo(@NonNull String enrollment) {
        currentActivity.startActivity(DashboardActivity.create(
                currentActivity, enrollment));
        currentActivity.startActivity(FormActivity.create(
                currentActivity, FormViewArguments.createForEnrollment(enrollment)));
        currentActivity.finish();
    }
}
