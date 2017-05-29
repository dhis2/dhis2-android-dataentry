package org.hisp.dhis.android.dataentry.reports;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.reports.search.SearchArguments;
import org.hisp.dhis.android.dataentry.reports.search.SearchFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportsActivity extends AppCompatActivity {
    static final String ARG_REPORTS_FRAGMENT = "tag:reportsFragment";
    static final String ARG_REPORTS_ARGUMENTS = "arg:reportsArguments";

    static final String ARG_SEARCH_FRAGMENT = "tag:searchFragment";
    static final String ARG_SEARCH_ARGUMENTS = "arg:searchArguments";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @NonNull
    public static Intent createIntent(@NonNull Activity activity,
            @NonNull ReportsArguments reportsArguments) {
        Intent intent = new Intent(activity, ReportsActivity.class);
        intent.putExtra(ARG_REPORTS_ARGUMENTS, reportsArguments);
        return intent;
    }

    @NonNull
    public static Intent createIntent(@NonNull Activity activity,
            @NonNull SearchArguments searchArguments) {
        Intent intent = new Intent(activity, ReportsActivity.class);
        intent.putExtra(ARG_SEARCH_ARGUMENTS, searchArguments);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        ButterKnife.bind(this);

        ReportsArguments reportsArguments = getIntent()
                .getExtras().getParcelable(ARG_REPORTS_ARGUMENTS);

        SearchArguments searchArguments = getIntent()
                .getExtras().getParcelable(ARG_SEARCH_ARGUMENTS);

        if (reportsArguments == null && searchArguments == null) {
            throw new IllegalStateException("ReportsArguments or SearchArguments must be supplied.");
        }

        String entityName = reportsArguments == null ?
                searchArguments.entityName() : reportsArguments.entityName();

        setSupportActionBar(toolbar);
        setTitle(entityName);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        // if reports fragment has been attached, we don't want to replace it
        if (reportsArguments != null && getSupportFragmentManager()
                .findFragmentByTag(ARG_REPORTS_FRAGMENT) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame, ReportsFragment
                            .create(reportsArguments), ARG_REPORTS_FRAGMENT)
                    .commitNow();
        }

        // if search fragment has been attached, we don't want to replace it
        if (searchArguments != null && getSupportFragmentManager()
                .findFragmentByTag(ARG_SEARCH_FRAGMENT) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame, new SearchFragment(), ARG_REPORTS_FRAGMENT)
                    .commitNow();
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
