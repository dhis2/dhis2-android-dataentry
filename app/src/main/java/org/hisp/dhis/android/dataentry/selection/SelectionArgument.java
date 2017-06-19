package org.hisp.dhis.android.dataentry.selection;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class SelectionArgument implements Parcelable {
    public enum Type {
        ORGANISATION, PROGRAM, PROGRAM_STAGE
    }

    @NonNull
    public abstract String uid();

    @NonNull
    public abstract String name();

    @NonNull
    public abstract Type type();

    @NonNull
    public static SelectionArgument create(@NonNull String uid,
            @NonNull String name, @NonNull Type type) {
        return new AutoValue_SelectionArgument(uid, name, type);
    }
}
