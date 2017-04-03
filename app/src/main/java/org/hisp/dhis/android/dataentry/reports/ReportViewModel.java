package org.hisp.dhis.android.dataentry.reports;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import java.util.Collections;
import java.util.List;

@AutoValue
abstract class ReportViewModel {
    public static final String TYPE_TEIS = "type:teis";
    public static final String TYPE_EVENTS = "type:events";
    public static final String TYPE_ENROLLMENTS = "type:enrollments";

    @NonNull
    public abstract String id();

    @NonNull
    public abstract Status status();

    @NonNull
    public abstract List<String> labels();

    @NonNull
    public static ReportViewModel create(@NonNull String id,
            @NonNull Status status, @NonNull List<String> labels) {
        return new AutoValue_ReportViewModel(id, status, Collections.unmodifiableList(labels));
    }

    enum Status {
        SYNCED, TO_SYNC, FAILED
    }
}
