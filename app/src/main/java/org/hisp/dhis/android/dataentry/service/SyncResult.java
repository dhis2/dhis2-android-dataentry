package org.hisp.dhis.android.dataentry.service;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

@AutoValue
abstract class SyncResult {

    @NonNull
    abstract Boolean isIdle();

    @NonNull
    abstract Boolean inProgress();

    @NonNull
    abstract Boolean isSuccess();

    @NonNull
    abstract String message();

    @NonNull
    static SyncResult idle() {
        return new AutoValue_SyncResult(true, false, false, "");
    }

    @NonNull
    static SyncResult progress() {
        return new AutoValue_SyncResult(false, true, false, "");
    }

    @NonNull
    static SyncResult success() {
        return new AutoValue_SyncResult(false, false, true, "");
    }

    @NonNull
    static SyncResult failure(@NonNull String message) {
        return new AutoValue_SyncResult(false, false, false, message);
    }
}
