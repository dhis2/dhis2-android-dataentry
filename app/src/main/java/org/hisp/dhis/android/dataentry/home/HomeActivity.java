/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.dataentry.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hisp.dhis.android.dataentry.R;

import butterknife.BindView;

import static org.hisp.dhis.android.dataentry.utils.Preconditions.isNull;

public class HomeActivity extends AppCompatActivity implements HomeView,
        NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener {
    //NavigationCallback,
    //OnBackPressedFromFragmentCallback

    private HomePresenter homePresenter;

    private static final int DEFAULT_ORDER_IN_CATEGORY = 100;

    // Drawer layout
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    // Showing information about user in navigation drawer
    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    @BindView(R.id.textview_username_initials)
    TextView usernameInitials;
    @BindView(R.id.textview_username)
    TextView username;
    @BindView(R.id.textview_user_info)
    TextView userInfo;

    // Delaying attachment of fragment
    // in order to avoid animation lag
    private Runnable pendingRunnable;

    @NonNull
    public static Intent createIntent(Activity activity) {
        return new Intent(activity, HomeActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//
//        homePresenter = new HomePresenterImpl();
//
//        drawerLayout.addDrawerListener(this);
//        navigationView.setNavigationItemSelectedListener(this);
//        navigationView.inflateMenu(R.menu.menu_drawer_default);
//
//        ViewGroup navigationHeader = (ViewGroup) getLayoutInflater()
//                .inflate(R.layout.navigation_header, navigationView, false);
//
//        navigationView.addHeaderView(navigationHeader);
//
//        toggleNavigationDrawer();

        /* select HomeFragment as default
        if (savedInstanceState == null) {
            onNavigationItemSelected(getNavigationView().getMenu()
                    .findItem(DRAWER_ITEM_EVENTS_ID));
        }*/
    }

    public void toggleNavigationDrawer() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        } else {
            drawerLayout.openDrawer(navigationView);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        super.onBackPressed();
    }

    public boolean onBackPressedFromFragment() {
        // When back button is pressed from a fragment, show the first menu item
        MenuItem firstMenuItem = navigationView.getMenu().getItem(0);
        onNavigationItemSelected(firstMenuItem);
        return true;
    }

    protected void attachFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    protected void attachFragmentDelayed(final Fragment fragment) {
        isNull(fragment, "Fragment must not be null");

        pendingRunnable = () -> attachFragment(fragment);
    }

    protected MenuItem addMenuItem(int menuItemId, Drawable icon, CharSequence title) {
        MenuItem menuItem = navigationView.getMenu().add(
                R.id.drawer_group_main, menuItemId, DEFAULT_ORDER_IN_CATEGORY, title);
        menuItem.setIcon(icon);
        menuItem.setCheckable(true);
        return menuItem;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        boolean isSelected = false;

        int menuItemId = menuItem.getItemId();

        if (menuItemId == R.id.drawer_item_profile) {
            attachFragmentDelayed(getProfileFragment());
            isSelected = true;
        } else if (menuItemId == R.id.drawer_item_settings) {
            attachFragmentDelayed(getSettingsFragment());
            isSelected = true;
        } else if (menuItemId == R.id.drawer_item_information) {
            attachFragment(getInformationFragment());
            isSelected = true;
        }

        if (isSelected) {
            navigationView.setCheckedItem(menuItemId);
            drawerLayout.closeDrawers();
        }

        return isSelected;
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        pendingRunnable = null;
        homePresenter.calculateLastSyncedPeriod();
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

//    @Override
//    protected void onResume() {
//        super.onResume();
//        homePresenter.onAttach(this);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        homePresenter.onDetach();
//    }

    @Override
    public void showLastSyncedMessage(String message) {
        navigationView.getMenu().findItem(R.id.drawer_item_synchronized)
                .setTitle(message);
    }

    @NonNull
    protected Fragment getProfileFragment() {
        /*return WrapperFragment.newInstance(DefaultProfileFragment.class,
                getString(R.string.drawer_item_profile));*/
        return new Fragment();
    }

    @NonNull
    protected Fragment getSettingsFragment() {
        /*return WrapperFragment.newInstance(DefaultSettingsFragment.class,
                getString(R.string.drawer_item_settings));*/
        return new Fragment();
    }

    @Override
    public void setUsername(CharSequence username) {
        this.username.setText(username);
    }

    @Override
    public void setUserInfo(CharSequence userInfo) {
        this.userInfo.setText(userInfo);
    }

    @Override
    public void setUserInitials(CharSequence userInitials) {
        this.usernameInitials.setText(userInitials);
    }

    public void setHomePresenter(HomePresenter homePresenter) {
        this.homePresenter = homePresenter;
    }

    protected Fragment getInformationFragment() {
        /*Bundle args = new Bundle();
        args.putString(InformationFragment.USERNAME,
        D2.me().userCredentials().toBlocking().first().getUsername());
        args.putString(InformationFragment.URL,
        new PreferencesModuleImpl(getContext()).getConfigurationPreferences().get().getServerUrl());

        return WrapperFragment.newInstance(InformationFragment.class,
                getString(R.string.drawer_item_information),
                args);*/
        return new Fragment();
    }
}
