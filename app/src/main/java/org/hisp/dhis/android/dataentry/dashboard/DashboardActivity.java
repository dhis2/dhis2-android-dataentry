package org.hisp.dhis.android.dataentry.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.dashboard.navigation.NavigationFragment;
import org.hisp.dhis.android.dataentry.dashboard.navigation.NavigationViewArguments;
import org.hisp.dhis.android.dataentry.form.FormFragment;
import org.hisp.dhis.android.dataentry.form.FormViewArguments;

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
        setContentView(R.layout.activity_dashboard);

        String enrollmentUid = isNull(
                getIntent().getStringExtra(ARG_ENROLLMENT_UID), "enrollmentUid == null");

        Boolean useTwoPaneLayout = findViewById(R.id.form) != null;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.navigation,
                        NavigationFragment.newInstance(NavigationViewArguments.create(enrollmentUid, useTwoPaneLayout)))
                .commit();

        if (useTwoPaneLayout) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.form,
                            FormFragment.newInstance(FormViewArguments.createForEvent("aygYgTAvuXy")))
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}