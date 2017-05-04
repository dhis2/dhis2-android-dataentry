package org.hisp.dhis.android.dataentry.reports;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ReportsArguments implements Parcelable {
    static final String TYPE_TEIS = "type:teis";
    static final String TYPE_EVENTS = "type:events";
    static final String TYPE_ENROLLMENTS = "type:enrollments";

    @NonNull
    public abstract String entityUid();

    @NonNull
    public abstract String entityName();

    @NonNull
    public abstract String entityType();

    @NonNull
    public static ReportsArguments createForEnrollments(
            @NonNull String teiUid, @NonNull String teName) {
        return new AutoValue_ReportsArguments(teiUid, teName, TYPE_ENROLLMENTS);
    }

    @NonNull
    public static ReportsArguments createForEvents(
            @NonNull String programUid, @NonNull String programName) {
        return new AutoValue_ReportsArguments(programUid,
                programName, ReportsArguments.TYPE_EVENTS);
    }

    @NonNull
    public static ReportsArguments createForTeis(
            @NonNull String teiUid, @NonNull String teName) {
        return new AutoValue_ReportsArguments(teiUid, teName, TYPE_TEIS);
    }
}
