package org.hisp.dhis.android.dataentry.dashboard;

import android.support.v4.app.FragmentActivity;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.form.FormFragment;
import org.hisp.dhis.android.dataentry.form.FormViewArguments;

public class DualPaneDashboardNavigator implements DashboardNavigator {

    private final FragmentActivity currentActivity;

    public DualPaneDashboardNavigator(FragmentActivity currentActivity) {
        this.currentActivity = currentActivity;
    }

    @Override
    public void navigateToEvent(String eventUid) {
        currentActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.form, FormFragment.newInstance(
                        FormViewArguments.createForEvent(eventUid)))
                .commit();
    }

    @Override
    public void navigateToEnrollment(String enrollmentUid) {
        currentActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.form, FormFragment.newInstance(
                        FormViewArguments.createForEnrollment(enrollmentUid)))
                .commit();
    }
}