package org.hisp.dhis.android.dataentry.reports;

import android.app.Activity;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.create.CreateItemsActivity;
import org.hisp.dhis.android.dataentry.create.CreateItemsArgument;
import org.hisp.dhis.android.dataentry.form.FormActivity;
import org.hisp.dhis.android.dataentry.form.FormViewArguments;

import timber.log.Timber;

final class SingleEventsNavigatorImpl implements ReportsNavigator {

    @NonNull
    private final Activity currentActivity;

    @NonNull
    private final String createEventPrompt;

    SingleEventsNavigatorImpl(@NonNull Activity currentActivity,
            @NonNull String createEventPrompt) {
        this.currentActivity = currentActivity;
        this.createEventPrompt = createEventPrompt;
    }

    @Override
    public void navigateTo(@NonNull String eventUid) {
        Timber.d("navigateTo(): %s", eventUid);
        currentActivity.startActivity(FormActivity.create(currentActivity,
                FormViewArguments.createForEvent(eventUid)));
    }

    @Override
    public void createFor(@NonNull String programUid) {
        Timber.d("createFor(): %s", programUid);
        currentActivity.startActivity(CreateItemsActivity.createIntent(currentActivity,
                CreateItemsArgument.create(programUid, createEventPrompt, CreateItemsArgument.Type.EVENT)));
    }
}
