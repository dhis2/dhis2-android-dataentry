package org.hisp.dhis.android.dataentry.reports;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.ui.Navigator;

final class SingleEventsNavigatorImpl implements Navigator {

    @NonNull
    private final String eventUid;

    public SingleEventsNavigatorImpl(@NonNull String eventUid) {
        this.eventUid = eventUid;
    }

    @Override
    public void navigateTo() {
        // ToDo: navigate to data entry
    }
}
