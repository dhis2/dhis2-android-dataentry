package org.hisp.dhis.android.dataentry.reports;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import org.hisp.dhis.android.dataentry.R;

public class ReportsActivity extends AppCompatActivity {
    private static final String ARG_FORM_UID = "arg:formUid";

    public static Intent create(@NonNull Activity activity, @NonNull String programUid) {
        Intent intent = new Intent(activity, ReportsActivity.class);
        intent.putExtra(ARG_FORM_UID, programUid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, ReportsFragment.create(getFormUid()))
                .commitNow();
    }

    private String getFormUid() {
        return getIntent().getExtras().getString(ARG_FORM_UID, "");
    }
}
