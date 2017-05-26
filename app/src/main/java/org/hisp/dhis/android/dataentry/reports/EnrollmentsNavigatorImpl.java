package org.hisp.dhis.android.dataentry.reports;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.dataentry.DhisApp;
import org.hisp.dhis.android.dataentry.form.dataentry.DataEntryActivity;
import org.hisp.dhis.android.dataentry.form.dataentry.DataEntryArguments;

import timber.log.Timber;

final class EnrollmentsNavigatorImpl implements ReportsNavigator {

    @NonNull
    private final Activity currentActivity;

    @NonNull
    private final String programName;

    // ToDo: remove
    @NonNull
    private final BriteDatabase briteDatabase;

    public EnrollmentsNavigatorImpl(@NonNull Activity currentActivity, @NonNull String programName) {
        this.currentActivity = currentActivity;
        this.programName = programName;
        this.briteDatabase = ((DhisApp) currentActivity.getApplication())
                .appComponent().briteDatabase();
    }

    @Override
    public void navigateTo(@NonNull String enrollmentUid) {
        Timber.d("navigateTo(): %s", enrollmentUid);
        currentActivity.startActivity(DataEntryActivity.create(currentActivity,
                DataEntryArguments.forEnrollment(enrollmentUid)));
    }

    @Override
    public void createFor(@NonNull String trackedEntityInstanceUid) {
        Timber.d("createFor(): %s", trackedEntityInstanceUid);
    }
}
