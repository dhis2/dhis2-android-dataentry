package org.hisp.dhis.android.dataentry.espresso;

import android.support.annotation.NonNull;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hisp.dhis.android.dataentry.commons.views.CircleView;

public class CircleViewMatchers {
    private CircleViewMatchers() {
        // no instances
    }

    @NonNull
    public static Matcher<View> withFillColor(final int color) {
        return new BoundedMatcher<View, CircleView>(CircleView.class) {
            @Override
            public boolean matchesSafely(CircleView circleView) {
                return circleView.getFillColor() == color;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with circle fill color: " + color);
            }
        };
    }

    @NonNull
    public static Matcher<View> withStrokeColor(final int color) {
        return new BoundedMatcher<View, CircleView>(CircleView.class) {
            @Override
            public boolean matchesSafely(CircleView circleView) {
                return circleView.getStrokeColor() == color;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with circle stroke color: " + color);
            }
        };
    }
}
