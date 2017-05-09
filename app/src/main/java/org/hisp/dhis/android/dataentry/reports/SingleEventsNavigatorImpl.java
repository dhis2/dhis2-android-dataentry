package org.hisp.dhis.android.dataentry.reports;

import android.support.annotation.NonNull;

import timber.log.Timber;

final class SingleEventsNavigatorImpl implements ReportsNavigator {

    @Override
    public void navigateTo(@NonNull String eventUid) {
        Timber.d("navigateTo(): %s", eventUid);
    }

    @Override
    public void createFor(@NonNull String uid) {

    }
}
