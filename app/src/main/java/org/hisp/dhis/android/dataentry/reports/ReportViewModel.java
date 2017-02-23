package org.hisp.dhis.android.dataentry.reports;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
public abstract class ReportViewModel {

    @NonNull
    public abstract String id();

    @NonNull
    public abstract SyncStatus status();

    @NonNull
    public abstract List<String> labels();

    public static ReportViewModel

    @AutoValue.Builder
    public static abstract class Builder {
        public abstract Builder id(String id);

        public abstract Builder status(SyncStatus status);

        public abstract Builder labels(List<String> labels);

        public abstract ReportViewModel build();
    }

    public enum SyncStatus {
        SENT, TO_UPDATE, TO_POST, ERROR
    }
}
