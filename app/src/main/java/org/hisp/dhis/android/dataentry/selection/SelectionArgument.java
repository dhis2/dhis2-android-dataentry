package org.hisp.dhis.android.dataentry.selection;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class SelectionArgument implements Parcelable {

    @NonNull
    public abstract String uid();

    @NonNull
    public abstract String name();

    @NonNull
    public static SelectionArgument create(@NonNull String uid, @NonNull String name) {
        return new AutoValue_SelectionArgument(uid, name);
    }
}
