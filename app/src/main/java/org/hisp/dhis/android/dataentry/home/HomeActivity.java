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
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import static org.hisp.dhis.android.dataentry.utils.Preconditions.isNull;

public class HomeActivity extends AppCompatActivity implements HomeView,
        NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener {

    // drawer layout
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    // navigation header views
    TextView usernameInitials;
    TextView username;
    TextView userInfo;

//    @Inject
//    HomePresenter homePresenter;

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

//        ((Components) getApplicationContext()).userComponent()
//                .plus(new HomeModule())
//                .inject(this);

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
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        // homePresenter.onAttach(this);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        // homePresenter.onDetach();
//    }

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
        // homePresenter.showLastSyncedDate();
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
//        switch (menuItem.getItemId()) {
////            case R.id.drawer_item_forms: {
////                attachFragmentDelayed(new Fragment());
////                break;
////            }
////            case R.id.drawer_item_profile: {
////                attachFragmentDelayed(new Fragment());
////                break;
////            }
////            case R.id.drawer_item_settings: {
////                attachFragmentDelayed(new Fragment());
////                break;
////            }
//            default: {
//                throw new IllegalArgumentException();
//            }
//        }

        attachFragmentDelayed(new Fragment());

        navigationView.setCheckedItem(menuItem.getItemId());
        drawerLayout.closeDrawers();

        return true;
    }

    @Override
    public void showLastSyncedMessage(String message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUsername(CharSequence username) {
        // username.setText(username);
    }

    @Override
    public void setUserInfo(CharSequence userInfo) {
        // userInfo.setText(userInfo);
    }

    @Override
    public void setUserInitials(CharSequence userInitials) {
        // usernameInitials.setText(userInitials);
    }

//    private void toggleNavigationDrawer() {
//        if (drawerLayout.isDrawerOpen(navigationView)) {
//            drawerLayout.closeDrawer(navigationView);
//        } else {
//            drawerLayout.openDrawer(navigationView);
//        }
//    }

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
}
