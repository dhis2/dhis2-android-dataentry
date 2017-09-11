package org.hisp.dhis.android.dataentry.dashboard.navigation;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.event.EventStatus;

@AutoValue
abstract class EventViewModel {

    @NonNull
    abstract String uid();

    @NonNull
    abstract String title();

    @NonNull
    abstract String date();

    @NonNull
    abstract EventStatus eventStatus();

    @NonNull
    static EventViewModel create(@NonNull String uid, @NonNull String title, @NonNull String date,
                                 @NonNull EventStatus status) {
        return new AutoValue_EventViewModel(uid, title, date, status);
    }
}
