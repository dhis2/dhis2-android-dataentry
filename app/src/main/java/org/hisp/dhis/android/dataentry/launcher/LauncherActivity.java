package org.hisp.dhis.android.dataentry.launcher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.hisp.dhis.android.dataentry.AppComponent;
import org.hisp.dhis.android.dataentry.DhisApp;
import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.main.MainActivity;
import org.hisp.dhis.android.dataentry.login.LoginActivity;
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

        // injecting dependencies
        appComponent.plus(new LauncherModule(serverComponent)).inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        launcherPresenter.onAttach(this);
        launcherPresenter.isUserLoggedIn();
    }

    @Override
    protected void onPause() {
        super.onPause();
        launcherPresenter.onDetach();
    }

    @Override
    public void navigateToLoginView() {
        startActivity(LoginActivity.create(this));
        finish();
    }

    @Override
    public void navigateToHomeView() {
        startActivity(MainActivity.create(this));
        finish();
    }
}
