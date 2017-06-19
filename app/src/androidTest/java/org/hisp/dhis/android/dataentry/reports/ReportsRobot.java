package org.hisp.dhis.android.dataentry.reports;

import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static org.hamcrest.Matchers.is;
import static org.hisp.dhis.android.dataentry.commons.espresso.CustomViewMatchers.withToolbarTitle;

final class ReportsRobot {

    @NonNull
    ReportsRobot checkToolbarTitle(@NonNull String title) {
        onView(isAssignableFrom(Toolbar.class))
                .check(matches(withToolbarTitle(is(title))));
        return this;
    }
}
