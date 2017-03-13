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
    static final String ARG_FORM_UID = "arg:formUid";
    static final String ARG_FORM_NAME = "arg:formName";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    public static Intent create(@NonNull Activity activity, @NonNull String programUid, @NonNull String programName) {
        Intent intent = new Intent(activity, ReportsActivity.class);
        intent.putExtra(ARG_FORM_UID, programUid);
        intent.putExtra(ARG_FORM_NAME, programName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        ButterKnife.bind(this);

        setUpToolbar();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, ReportsFragment.create(getFormUid()))
                .commitNow();
    }

    private void setUpToolbar() {
        toolbar.setTitle(getFormName());
    }

    private String getFormUid() {
        return getIntent().getExtras().getString(ARG_FORM_UID, "");
    }

    private String getFormName() {
        return getIntent().getExtras().getString(ARG_FORM_NAME, "");
    }
}
