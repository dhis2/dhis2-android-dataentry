package org.hisp.dhis.android.dataentry.create;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.form.FormActivity;
import org.hisp.dhis.android.dataentry.form.FormViewArguments;

final class SingleEventsNavigator implements CreateItemsNavigator {

    @NonNull
    private final CreateItemsActivity currentActivity;

    SingleEventsNavigator(@NonNull CreateItemsActivity currentActivity) {
        this.currentActivity = currentActivity;
    }

    @Override
    public void navigateTo(@NonNull String event) {
        currentActivity.startActivity(FormActivity.create(currentActivity,
                FormViewArguments.createForEvent(event)));
        currentActivity.finish();
    }
}
