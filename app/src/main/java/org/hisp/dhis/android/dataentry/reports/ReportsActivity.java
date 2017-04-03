package org.hisp.dhis.android.dataentry.reports;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.hisp.dhis.android.dataentry.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportsActivity extends AppCompatActivity {
    static final String ARG_ENTITY_UID = "arg:entityUid";
    static final String ARG_ENTITY_NAME = "arg:entityName";
    static final String ARG_ENTITY_TYPE = "arg:entityType";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @NonNull
    public static Intent createIntentForTeis(@NonNull Activity activity,
            @NonNull String teUid, @NonNull String teName) {
        Intent intent = new Intent(activity, ReportsActivity.class);
        intent.putExtra(ARG_ENTITY_UID, teUid);
        intent.putExtra(ARG_ENTITY_NAME, teName);
        intent.putExtra(ARG_ENTITY_TYPE, ReportViewModel.TYPE_TEIS);
        return intent;
    }

    @NonNull
    public static Intent createIntentForEvents(@NonNull Activity activity,
            @NonNull String programUid, @NonNull String programName) {
        Intent intent = new Intent(activity, ReportsActivity.class);
        intent.putExtra(ARG_ENTITY_UID, programUid);
        intent.putExtra(ARG_ENTITY_NAME, programName);
        intent.putExtra(ARG_ENTITY_TYPE, ReportViewModel.TYPE_EVENTS);
        return intent;
    }

    @NonNull
    public static Intent createIntentForEnrollments(@NonNull Activity activity,
            @NonNull String teiUid, @NonNull String teName) {
        Intent intent = new Intent(activity, ReportsActivity.class);
        intent.putExtra(ARG_ENTITY_UID, teiUid);
        intent.putExtra(ARG_ENTITY_NAME, teName);
        intent.putExtra(ARG_ENTITY_TYPE, ReportViewModel.TYPE_ENROLLMENTS);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        ButterKnife.bind(this);

        setUpToolbar();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, reportsFragment())
                .commitNow();
    }

    @NonNull
    private ReportsFragment reportsFragment() {
        switch (getIntent().getStringExtra(ARG_ENTITY_TYPE)) {
            case ReportViewModel.TYPE_TEIS:
                return ReportsFragment.createForTeis(getEntityUid(), getEntityName());
            case ReportViewModel.TYPE_EVENTS:
                return ReportsFragment.createForEvents(getEntityUid(), getEntityName());
            case ReportViewModel.TYPE_ENROLLMENTS:
                return ReportsFragment.createForEnrollments(getEntityUid(), getEntityName());
            default:
                throw new IllegalArgumentException("Unsupported entity type: " +
                        getIntent().getStringExtra(ARG_ENTITY_TYPE));
        }
    }

    private void setUpToolbar() {
        toolbar.setTitle(getEntityName());
    }

    private String getEntityUid() {
        return getIntent().getExtras().getString(ARG_ENTITY_UID, "");
    }

    private String getEntityName() {
        return getIntent().getExtras().getString(ARG_ENTITY_NAME, "");
    }
}
