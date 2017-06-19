package org.hisp.dhis.android.dataentry.reports;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ReportViewModel {

    @NonNull
    public abstract Status status();

    @NonNull
    public abstract String id();

    @NonNull
    public abstract String labels();

    @NonNull
    public static ReportViewModel create(@NonNull Status status,
            @NonNull String id, @NonNull String labels) {
        return new AutoValue_ReportViewModel(status, id, labels);
    }

    public enum Status {
        SYNCED, TO_SYNC, FAILED
    }
}
