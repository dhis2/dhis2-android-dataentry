package org.hisp.dhis.android.dataentry.form.dataentry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.dataentry.DhisApp;
import org.hisp.dhis.android.dataentry.R;

import java.util.UUID;

public class DataEntryActivity extends AppCompatActivity {

    @NonNull
    public static Intent create(@NonNull Activity activity) {
        return new Intent(activity, DataEntryActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_entry);

        DataEntryArguments arguments = DataEntryArguments.forEvent(create().uid());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_place_holder, DataEntryFragment.create(arguments))
                .commitNow();
    }

    @NonNull
    private EventModel create() {
        EventModel event = EventModel.builder()
                .uid(UUID.randomUUID().toString())
                .program("VBqh0ynB2wv")
                .programStage("pTo4uMt3xur")
                .organisationUnit("DiszpKrYNg8")
                .build();

        ((DhisApp) getApplication()).appComponent().briteDatabase()
                .insert(EventModel.TABLE, event.toContentValues());

        return event;
    }
}
