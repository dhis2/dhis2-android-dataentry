package org.hisp.dhis.android.dataentry.reports;

import android.support.annotation.NonNull;

import timber.log.Timber;

final class EnrollmentsNavigatorImpl implements ReportsNavigator {

    @Override
    public void navigateTo(@NonNull String enrollmentUid) {
        Timber.d("navigateTo(): %s", enrollmentUid);
    }
}
