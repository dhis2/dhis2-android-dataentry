package org.hisp.dhis.android.dataentry.dashboard.navigation;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class NavigationViewArguments {

    @NonNull
    public abstract String enrollmentUid();

    @NonNull
    public abstract Boolean twoPaneLayout();

    public static NavigationViewArguments create(@NonNull String enrollmentUid, @NonNull Boolean twoPaneLayout) {
        return new AutoValue_NavigationViewArguments(enrollmentUid, twoPaneLayout);
    }

}
