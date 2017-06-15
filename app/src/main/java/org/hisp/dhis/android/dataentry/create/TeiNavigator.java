package org.hisp.dhis.android.dataentry.create;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.reports.ReportsActivity;
import org.hisp.dhis.android.dataentry.reports.ReportsArguments;

final class TeiNavigator implements CreateItemsNavigator {

    @NonNull
    private final CreateItemsActivity currentActivity;

    @NonNull
    private final String teName;

    TeiNavigator(@NonNull CreateItemsActivity activity, @NonNull String teName) {
        this.currentActivity = activity;
        this.teName = teName;
    }

    @Override
    public void navigateTo(@NonNull String tei) {
        currentActivity.startActivity(ReportsActivity.createIntent(currentActivity,
                ReportsArguments.createForEnrollments(tei, teName)));
        currentActivity.finish();
    }
}
