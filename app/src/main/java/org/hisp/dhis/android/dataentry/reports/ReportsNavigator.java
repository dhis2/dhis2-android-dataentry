package org.hisp.dhis.android.dataentry.reports;

import android.support.annotation.NonNull;

public interface ReportsNavigator {
    void navigateTo(@NonNull String uid);

    void createFor(@NonNull String uid);
}
