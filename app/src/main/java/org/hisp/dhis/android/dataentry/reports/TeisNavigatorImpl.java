package org.hisp.dhis.android.dataentry.reports;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.ui.Navigator;

final class TeisNavigatorImpl implements Navigator {

    @NonNull
    private final String teiUid;

    public TeisNavigatorImpl(@NonNull String teiUid) {
        this.teiUid = teiUid;
    }

    @Override
    public void navigateTo() {
        // ToDo: navigate to list of enrollments
    }
}
