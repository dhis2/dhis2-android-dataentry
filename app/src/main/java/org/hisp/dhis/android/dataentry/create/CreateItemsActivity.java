package org.hisp.dhis.android.dataentry.create;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.hisp.dhis.android.dataentry.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateItemsActivity extends AppCompatActivity {

    static final String TAG_CREATE = "tag:createItems";
    static final String ARG_CREATION = "arg:creationArguments";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @NonNull
    public static Intent createIntent(@NonNull Activity activity, @NonNull CreateItemsArgument argument) {
        Intent intent = new Intent(activity, CreateItemsActivity.class);
        intent.putExtra(ARG_CREATION, argument);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        ButterKnife.bind(this);

        CreateItemsArgument argument = getIntent().getExtras().getParcelable(ARG_CREATION);
        if (argument == null) {
            throw new IllegalStateException("CreateItemsArgument must be supplied!");
        }

        setupToolbar(toolbar, argument.name());
        attachCreateFragment(argument);
    }

    private void attachCreateFragment(CreateItemsArgument arg) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content_frame, CreateItemsFragment.create(arg), TAG_CREATE)
                .commitNow();
    }

    private void setupToolbar(@NonNull Toolbar toolbar, @NonNull String name) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(name);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
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
