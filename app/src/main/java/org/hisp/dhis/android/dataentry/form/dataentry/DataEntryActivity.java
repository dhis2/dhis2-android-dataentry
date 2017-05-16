package org.hisp.dhis.android.dataentry.form.dataentry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import org.hisp.dhis.android.dataentry.R;

public class DataEntryActivity extends AppCompatActivity {
    private static final String ARGS = "args";

    @NonNull
    public static Intent create(@NonNull Activity activity, @NonNull DataEntryArguments arguments) {
        Intent intent = new Intent(activity, DataEntryActivity.class);
        intent.putExtra(ARGS, arguments);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_entry);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_place_holder, DataEntryFragment
                        .create(getIntent().getParcelableExtra(ARGS)))
                .commitNow();
    }
}
