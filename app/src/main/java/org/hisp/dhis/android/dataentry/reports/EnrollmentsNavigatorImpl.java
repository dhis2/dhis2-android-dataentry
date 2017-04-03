package org.hisp.dhis.android.dataentry.reports;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.ui.Navigator;

final class EnrollmentsNavigatorImpl implements Navigator {

    @NonNull
    private final String enrollmentUid;

    public EnrollmentsNavigatorImpl(@NonNull String enrollmentUid) {
        this.enrollmentUid = enrollmentUid;
    }

    @Override
    public void navigateTo() {
        // ToDo: navigate to data entry
    }
}
