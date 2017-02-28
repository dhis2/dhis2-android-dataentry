package org.hisp.dhis.android.dataentry.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hisp.dhis.android.dataentry.Components;
import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.ToolbarFragment;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;

public class HomeActivity extends AppCompatActivity implements HomeView,
        NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener {

    // drawer layout
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    // navigation header views
    TextView usernameInitials;
    TextView username;
    TextView userInfo;

    @Inject
    HomePresenter homePresenter;

    // Delaying attachment of fragment
    // in order to avoid animation lag
    @Nullable
    Runnable pendingRunnable;

    @NonNull
    public static Intent create(@NonNull Activity activity) {
        return new Intent(activity, HomeActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ((Components) getApplicationContext()).userComponent()
                .plus(new HomeModule())
                .inject(this);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        drawerLayout.addDrawerListener(this);
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
                    .findItem(R.id.drawer_item_forms));
        }
    }

    @Override
    protected void onResume() {
        homePresenter.onAttach(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        homePresenter.onDetach();
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
        attachFragment(ToolbarFragment.create(menuItem.getTitle().toString()));

        navigationView.setCheckedItem(menuItem.getItemId());
        drawerLayout.closeDrawers();

        return true;
    }

    @NonNull
    @UiThread
    @Override
    public Consumer<String> showUsername() {
        return (username) -> this.username.setText(username);
    }

    @NonNull
    @UiThread
    @Override
    public Consumer<String> showUserInitials() {
        return (userInitials) -> this.usernameInitials.setText(userInitials);
    }

    @NonNull
    @UiThread
    @Override
    public Consumer<String> showUserInfo() {
        throw new UnsupportedOperationException();
    }

    protected void attachFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }
}
