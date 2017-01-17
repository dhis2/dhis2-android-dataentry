package org.hisp.dhis.android.dataentry.launcher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.hisp.dhis.android.dataentry.AppComponent;
import org.hisp.dhis.android.dataentry.DhisApp;
import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.server.ServerComponent;

import javax.inject.Inject;

public class LauncherActivity extends AppCompatActivity implements LauncherView {

    @Inject
    LauncherPresenter launcherPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        AppComponent appComponent = ((DhisApp) getApplicationContext()).appComponent();
        ServerComponent serverComponent = ((DhisApp) getApplicationContext()).serverComponent();

        // creating instance of LauncherComponent and
        // injecting dependencies into activity
        appComponent.plus(new LauncherModule(serverComponent))
                .inject(this);

        // Injection should happen here, but without constant interaction
        // with application (you should not keep everything in your application class)
        launcherPresenter.isUserLoggedIn();
    }

    @Override
    protected void onResume() {
        super.onResume();
        launcherPresenter.onAttach(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        launcherPresenter.onDetach();
    }

    @Override
    public void navigateToLoginView() {
        Toast.makeText(this, "navigateToLoginView()", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToHomeView() {
        Toast.makeText(this, "navigateToHomeView()", Toast.LENGTH_SHORT).show();
    }
}
