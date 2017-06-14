package org.hisp.dhis.android.dataentry.reports.search;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceModel;
import org.hisp.dhis.android.dataentry.DhisApp;
import org.hisp.dhis.android.dataentry.reports.ReportsActivity;
import org.hisp.dhis.android.dataentry.reports.ReportsArguments;
import org.hisp.dhis.android.dataentry.reports.ReportsNavigator;

import java.util.UUID;

import timber.log.Timber;

final class SearchNavigator implements ReportsNavigator {

    @NonNull
    private final Activity currentActivity;

    @NonNull
    private final String teName;

    // ToDo: remove
    @NonNull
    private final BriteDatabase briteDatabase;

    SearchNavigator(@NonNull Activity currentActivity, @NonNull String teName) {
        this.currentActivity = currentActivity;
        this.teName = teName;
        this.briteDatabase = ((DhisApp) currentActivity.getApplication())
                .appComponent().briteDatabase();
    }

    @Override
    public void navigateTo(@NonNull String teiUid) {
        Intent intent = ReportsActivity.createIntent(currentActivity,
                ReportsArguments.createForEnrollments(teiUid, teName));
        ActivityCompat.startActivity(currentActivity, intent, null);
    }

    @Override
    public void createFor(@NonNull String trackedEntity) {
        Timber.d("createFor(): %s", trackedEntity);

        TrackedEntityInstanceModel tei = TrackedEntityInstanceModel.builder()
                .uid(UUID.randomUUID().toString())
                .trackedEntity(trackedEntity)
                .organisationUnit("DiszpKrYNg8")
                .state(State.TO_POST)
                .build();

        // insert dummy tei
        Timber.d("Insert tei=[%d]", briteDatabase.insert(TrackedEntityInstanceModel.TABLE,
                tei.toContentValues()));

        EnrollmentModel enrollmentModel = EnrollmentModel.builder()
                .uid(UUID.randomUUID().toString())
                .trackedEntityInstance(tei.uid())
                .organisationUnit("DiszpKrYNg8")
                .program("ur1Edk5Oe2n")
                .state(State.TO_POST)
                .build();

        // insert dummy enrollment
        Timber.d("Insert enrollment=[%d]", briteDatabase.insert(EnrollmentModel.TABLE,
                enrollmentModel.toContentValues()));
    }
}
