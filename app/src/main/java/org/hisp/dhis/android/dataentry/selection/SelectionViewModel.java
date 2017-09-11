package org.hisp.dhis.android.dataentry.selection;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class SelectionViewModel implements Parcelable {

    @NonNull
    public abstract String uid();

    @NonNull
    public abstract String name();

    @NonNull
    public abstract String code();

    @NonNull
    public static SelectionViewModel create(@NonNull String uid,
            @NonNull String name, @NonNull String code) {
        return new AutoValue_SelectionViewModel(uid, name, code);
    }

    @NonNull
    public static SelectionViewModel create(@NonNull String uid,
            @NonNull String name) {
        return new AutoValue_SelectionViewModel(uid, name, "");
    }
}
