package org.hisp.dhis.android.dataentry.dashboard;

public interface DashboardNavigator {

    void navigateToEvent(String eventUid);

    void navigateToEnrollment(String enrollmentUid);
}
