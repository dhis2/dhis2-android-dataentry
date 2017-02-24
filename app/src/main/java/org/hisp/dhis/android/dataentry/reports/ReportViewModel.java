package org.hisp.dhis.android.dataentry.reports;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import java.util.Collections;
import java.util.List;

@AutoValue
abstract class ReportViewModel {

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
        SENT, TO_UPDATE, TO_POST, ERROR
    }
}
