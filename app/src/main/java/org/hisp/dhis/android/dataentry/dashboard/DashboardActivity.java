package org.hisp.dhis.android.dataentry.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import org.hisp.dhis.android.dataentry.R;

import static org.hisp.dhis.android.dataentry.commons.utils.Preconditions.isNull;

public class DashboardActivity extends AppCompatActivity {
    private static String ARG_ENROLLMENT_UID = "enrollmentUid";

    @NonNull
    public static Intent create(@NonNull Activity activity,
                                @NonNull String enrollmentUid) {
        isNull(activity, "activity must not be null");
        isNull(enrollmentUid, "enrollmentUid must not be null");

        Intent intent = new Intent(activity, DashboardActivity.class);
        intent.putExtra(ARG_ENROLLMENT_UID, enrollmentUid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, DashboardFragment.newInstance(
                        getIntent().getStringExtra(ARG_ENROLLMENT_UID)))
                .commit();

    }
}