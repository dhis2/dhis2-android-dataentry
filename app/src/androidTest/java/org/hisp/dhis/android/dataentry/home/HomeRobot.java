package org.hisp.dhis.android.dataentry.home;

import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.Toolbar;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.espresso.OrientationChangeAction;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.close;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.NavigationViewActions.navigateTo;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.isSelected;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hisp.dhis.android.dataentry.espresso.CustomViewMatchers.withToolbarTitle;

class HomeRobot {

    HomeRobot() {
        // explicit public constructor
    }

    HomeRobot openSlidingPanel() {
        onView(withId(R.id.drawer_layout))
                .perform(open(GravityCompat.START));
        return this;
    }

    HomeRobot closeSlidingPanel() {
        onView(withId(R.id.drawer_layout))
                .perform(close(GravityCompat.START));
        return this;
    }

    HomeRobot checkUserInitials(@NonNull String initials) {
        onView(withId(R.id.textview_username_initials))
                .check(matches(withText(initials)));
        return this;
    }

    HomeRobot checkUsername(@NonNull String username) {
        onView(withId(R.id.textview_username))
                .check(matches(withText(username)));
        return this;
    }

    HomeRobot checkUserInfo(@NonNull String userInfo) {
        onView(withId(R.id.textview_user_info))
                .check(matches(withText(userInfo)));
        return this;
    }

    HomeRobot checkToolbarTitle(@NonNull String title) {
        onView(isAssignableFrom(Toolbar.class))
                .check(matches(withToolbarTitle(is(title))));
        return this;
    }

    HomeRobot formsMenuItemIsSelected() {
        onView(withId(R.id.navigation_view))
                .perform(navigateTo(R.id.drawer_item_forms))
                .check(matches(isSelected()));
        return this;
    }

    HomeRobot rotateToPortrait() {
        onView(isRoot()).perform(OrientationChangeAction.orientationPortrait());
        return this;
    }

    HomeRobot rotateToLandscape() {
        onView(isRoot()).perform(OrientationChangeAction.orientationLandscape());
        return this;
    }
}
