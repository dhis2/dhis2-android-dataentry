package org.hisp.dhis.android.dataentry.main;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hisp.dhis.android.dataentry.Components;
import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.ui.DummyFragment;
import org.hisp.dhis.android.dataentry.main.home.HomeFragment;
import org.hisp.dhis.android.dataentry.service.SyncService;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity implements MainView,
        NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener {

    // drawer layout
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    // navigation header views
    TextView usernameInitials;
    TextView username;
    TextView userInfo;

    @Inject
    MainPresenter mainPresenter;

    // Delaying attachment of fragment
    // in order to avoid animation lag
    @Nullable
    Runnable pendingRunnable;

    // Handles toggling of the drawer from the hamburger icon, animating the icon
    // and keeping it in sync with drawer state
    private ActionBarDrawerToggle drawerToggle;

    @NonNull
    public static Intent create(@NonNull Activity activity) {
        return new Intent(activity, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ((Components) getApplicationContext()).userComponent()
                .plus(new MainModule())
                .inject(this);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        setupToolbar();

        drawerLayout.addDrawerListener(this);
        drawerLayout.addDrawerListener(drawerToggle);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.inflateMenu(R.menu.menu_drawer);

        ViewGroup navigationHeader = (ViewGroup) getLayoutInflater()
                .inflate(R.layout.navigation_header, navigationView, false);

        usernameInitials = (TextView) navigationHeader
                .findViewById(R.id.textview_username_initials);
        username = (TextView) navigationHeader
                .findViewById(R.id.textview_username);
        userInfo = (TextView) navigationHeader
                .findViewById(R.id.textview_user_info);

        navigationView.addHeaderView(navigationHeader);

        if (savedInstanceState == null) {
            onNavigationItemSelected(navigationView.getMenu()
                    .findItem(R.id.drawer_item_home));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    @Override
    protected void onResume() {
        mainPresenter.onAttach(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mainPresenter.onDetach();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        pendingRunnable = null;
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        if (pendingRunnable != null) {
            new Handler().post(pendingRunnable);
        }

        pendingRunnable = null;
    }

    @Override
    public void onDrawerStateChanged(int newState) {
        // no-op
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        // no-op
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        navigationView.setCheckedItem(menuItem.getItemId());
        getSupportActionBar().setTitle(menuItem.getTitle());

        if (menuItem.getItemId() == R.id.drawer_item_home) {
            attachFragment(new HomeFragment());
        } else {
            attachFragment(new DummyFragment());
        }
        drawerLayout.closeDrawers();

        return true;
    }

    @NonNull
    @UiThread
    @Override
    public Consumer<String> renderUsername() {
        return username1 -> username.setText(username1);
    }

    @NonNull
    @UiThread
    @Override
    public Consumer<String> renderUserInitials() {
        return (userInitials) -> this.usernameInitials.setText(userInitials);
    }

    @NonNull
    @UiThread
    @Override
    public Consumer<String> renderUserInfo() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    protected void attachFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else if (item.getItemId() == R.id.action_sync) {
            startService(new Intent(getApplicationContext(), SyncService.class));
            return true;
        }

        return super.onOptionsItemSelected(item);

    }
}
