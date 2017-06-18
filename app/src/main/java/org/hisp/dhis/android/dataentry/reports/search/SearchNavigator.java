package org.hisp.dhis.android.dataentry.reports.search;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import org.hisp.dhis.android.dataentry.create.CreateItemsActivity;
import org.hisp.dhis.android.dataentry.create.CreateItemsArgument;
import org.hisp.dhis.android.dataentry.reports.ReportsActivity;
import org.hisp.dhis.android.dataentry.reports.ReportsArguments;
import org.hisp.dhis.android.dataentry.reports.ReportsNavigator;

import timber.log.Timber;

final class SearchNavigator implements ReportsNavigator {

    @NonNull
    private final Activity currentActivity;

    @NonNull
    private final String teName;

    SearchNavigator(@NonNull Activity currentActivity, @NonNull String teName) {
        this.currentActivity = currentActivity;
        this.teName = teName;
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

        currentActivity.startActivity(CreateItemsActivity.createIntent(currentActivity,
                CreateItemsArgument.create(trackedEntity, teName, CreateItemsArgument.Type.TEI)));

        /*TrackedEntityInstanceModel tei = TrackedEntityInstanceModel.builder()
                .uid(UUID.randomUUID().toString())
                .trackedEntity(trackedEntity)
                .organisationUnit("DiszpKrYNg8")
                .state(State.TO_POST)
                .build();

        // insert dummy tei
        Timber.d("Insert tei=[%d]", briteDatabase.insert(TrackedEntityInstanceModel.TABLE,
                tei.toContentValues()));


        navigateTo(tei.uid());

        // insert dummy enrollment
        Timber.d("Insert enrollment=[%d]", briteDatabase.insert(EnrollmentModel.TABLE,
                enrollmentModel.toContentValues()));*/
    }
}
