package org.hisp.dhis.android.dataentry.dashboard;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.event.EventStatus;

@AutoValue
abstract class EventViewModel {

    @NonNull
    abstract String title();

    @NonNull
    abstract String date();

    @NonNull
    abstract EventStatus eventStatus();

    @NonNull
    static EventViewModel create(@NonNull String title, @NonNull String date, @NonNull EventStatus status) {
        return new AutoValue_EventViewModel(title, date, status);
    }
}
