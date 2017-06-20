package org.hisp.dhis.android.dataentry.dashboard;

import android.app.Activity;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.form.FormActivity;
import org.hisp.dhis.android.dataentry.form.FormViewArguments;

class SinglePaneDashboardNavigator implements DashboardNavigator {

    private final Activity currentActivity;

    SinglePaneDashboardNavigator(@NonNull Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    @Override
    public void navigateToEvent(String eventUid) {
        currentActivity.startActivity(
                FormActivity.create(currentActivity, FormViewArguments.createForEvent(eventUid))
        );
    }

    @Override
    public void navigateToEnrollment(String enrollmentUid) {
        currentActivity.startActivity(
                FormActivity.create(currentActivity, FormViewArguments.createForEnrollment(enrollmentUid)));
    }
}
