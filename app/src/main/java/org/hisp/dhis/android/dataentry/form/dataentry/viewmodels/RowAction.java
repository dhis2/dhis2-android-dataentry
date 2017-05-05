package org.hisp.dhis.android.dataentry.form.dataentry.viewmodels;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class RowAction {

    @NonNull
    public abstract String id();

    @NonNull
    public abstract String value();

    @NonNull
    public static RowAction create(@NonNull String id, @NonNull String value) {
        return new AutoValue_RowAction(id, value);
    }
}
