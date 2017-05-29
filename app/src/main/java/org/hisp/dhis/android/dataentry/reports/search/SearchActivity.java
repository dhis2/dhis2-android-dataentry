package org.hisp.dhis.android.dataentry.reports.search;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.hisp.dhis.android.dataentry.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {
    private static String TAG_SEARCH_FRAGMENT = "tag:searchFragment";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        if (getSupportFragmentManager()
                .findFragmentByTag(TAG_SEARCH_FRAGMENT) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame, new Fragment(), TAG_SEARCH_FRAGMENT)
                    .commitNow();
        }
    }
}
