package org.hisp.dhis.android.dataentry.form;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import org.hisp.dhis.android.dataentry.R;

import static org.hisp.dhis.android.dataentry.commons.utils.Preconditions.isNull;

public class FormActivity extends AppCompatActivity {

    private static String ARG_EVENT = "formViewArguments";

    public static void startActivity(@NonNull Activity activity, @NonNull FormViewArguments formViewArguments) {
        isNull(activity, "activity must not be null");
        isNull(formViewArguments, "formViewArguments must not be null");

        Intent intent = new Intent(activity, FormActivity.class);
        intent.putExtra(ARG_EVENT, formViewArguments);
        activity.startActivity(intent);
    }

    public FormActivity() {
        // required empty constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        FormViewArguments formViewArguments = getIntent().getParcelableExtra(ARG_EVENT);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, FormFragment.newInstance(formViewArguments))
                .commit();

    }
}