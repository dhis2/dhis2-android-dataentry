package org.hisp.dhis.android.dataentry.launcher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.hisp.dhis.android.dataentry.R;

public class LauncherActivity extends AppCompatActivity implements LauncherView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
    }

    @Override
    public void navigateToLoginView() {
        // stub
    }

    @Override
    public void navigateToHomeView() {
        // stub
    }
}
