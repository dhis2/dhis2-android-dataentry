package org.hisp.dhis.android.dataentry.dashboard;

interface DashboardNavigator {

    void navigateToEvent(String eventUid);

    void navigateToEnrollment(String enrollmentUid);
}
