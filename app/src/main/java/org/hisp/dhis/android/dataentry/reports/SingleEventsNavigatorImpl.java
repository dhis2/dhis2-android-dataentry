package org.hisp.dhis.android.dataentry.reports;

import android.app.Activity;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.dataentry.DhisApp;
import org.hisp.dhis.android.dataentry.create.CreateItemsActivity;
import org.hisp.dhis.android.dataentry.create.CreateItemsArgument;
import org.hisp.dhis.android.dataentry.form.FormActivity;
import org.hisp.dhis.android.dataentry.form.FormViewArguments;

import timber.log.Timber;

final class SingleEventsNavigatorImpl implements ReportsNavigator {

    @NonNull
    private final Activity currentActivity;

    // ToDo: remove
    @NonNull
    private final BriteDatabase briteDatabase;

    SingleEventsNavigatorImpl(@NonNull Activity currentActivity) {
        this.currentActivity = currentActivity;
        this.briteDatabase = ((DhisApp) currentActivity.getApplication())
                .appComponent().briteDatabase();
    }

    @Override
    public void navigateTo(@NonNull String eventUid) {
        Timber.d("navigateTo(): %s", eventUid);
//        currentActivity.startActivity(DataEntryActivity.create(currentActivity,
//                DataEntryArguments.forEvent(eventUid)));

        currentActivity.startActivity(FormActivity.create(currentActivity,
                FormViewArguments.createForEvent(eventUid)));
    }

    @Override
    public void createFor(@NonNull String programUid) {
        Timber.d("createFor(): %s", programUid);
        //TODO: find a way to put a transatable heading in here: New Event or just the Program name ?..etc
        currentActivity.startActivity(CreateItemsActivity.createIntent(currentActivity,
                CreateItemsArgument.create("New Event", programUid, CreateItemsArgument.Type.EVENT)));

     /*   EventModel event = EventModel.builder()
                .uid(UUID.randomUUID().toString())
                .program(programUid)
                .programStage(programStage(programUid))
                .organisationUnit("DiszpKrYNg8")
                .state(State.TO_POST)
                .status(EventStatus.ACTIVE)
                .build();

        briteDatabase.insert(EventModel.TABLE, event.toContentValues());*/
    }

    @NonNull
    private String programStage(@NonNull String programUid) {
        Cursor cursor = briteDatabase.query("SELECT uid FROM ProgramStage " +
                "WHERE program = ? LIMIT 1;", programUid);
        try {
            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        } finally {
            cursor.close();
        }

        return null;
    }
}
