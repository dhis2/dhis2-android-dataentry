package org.hisp.dhis.android.dataentry.dashboard.navigation;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Flowable;

interface NavigationRepository {

    @NonNull
    Flowable<String> program(@NonNull String enrollmentUid);

    @NonNull
    Flowable<List<String>> attributes(@NonNull String enrollmentUid);

    @NonNull
    Flowable<List<EventViewModel>> events(@NonNull String enrollmentUid);
}
