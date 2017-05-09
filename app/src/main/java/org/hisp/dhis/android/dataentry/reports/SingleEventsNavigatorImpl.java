package org.hisp.dhis.android.dataentry.reports;

import android.app.Activity;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.dataentry.DhisApp;
import org.hisp.dhis.android.dataentry.form.dataentry.DataEntryActivity;
import org.hisp.dhis.android.dataentry.form.dataentry.DataEntryArguments;

import java.util.UUID;

import timber.log.Timber;

final class SingleEventsNavigatorImpl implements ReportsNavigator {

    @NonNull
    private final Activity currentActivity;

    @NonNull
    private final String programName;

    // ToDo: remove
    @NonNull
    private final BriteDatabase briteDatabase;

    SingleEventsNavigatorImpl(@NonNull Activity currentActivity, @NonNull String programName) {
        this.currentActivity = currentActivity;
        this.programName = programName;
        this.briteDatabase = ((DhisApp) currentActivity.getApplication())
                .appComponent().briteDatabase();
    }

    @Override
    public void navigateTo(@NonNull String eventUid) {
        Timber.d("navigateTo(): %s", eventUid);
        currentActivity.startActivity(DataEntryActivity.create(currentActivity,
                DataEntryArguments.forEvent(eventUid)));
    }

    @Override
    public void createFor(@NonNull String programUid) {
        Timber.d("createFor(): %s", programUid);

        EventModel event = EventModel.builder()
                .uid(UUID.randomUUID().toString())
                .program(programUid)
                .programStage(programStage(programUid))
                .organisationUnit("DiszpKrYNg8")
                .state(State.TO_POST)
                .build();

        briteDatabase.insert(EventModel.TABLE, event.toContentValues());
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
