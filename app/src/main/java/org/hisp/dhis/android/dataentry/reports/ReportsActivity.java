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
    static final String ARG_ARGUMENTS = "arg:arguments";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @NonNull
    public static Intent createIntent(@NonNull Activity activity,
            @NonNull ReportsArguments reportsArguments) {
        Intent intent = new Intent(activity, ReportsActivity.class);
        intent.putExtra(ARG_ARGUMENTS, reportsArguments);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        ButterKnife.bind(this);

        ReportsArguments reportsArguments = getIntent()
                .getExtras().getParcelable(ARG_ARGUMENTS);

        if (reportsArguments == null) {
            throw new IllegalStateException("ReportsArguments must be supplied.");
        }

        toolbar.setTitle(reportsArguments.entityName());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, ReportsFragment.create(reportsArguments))
                .commitNow();
    }
}
