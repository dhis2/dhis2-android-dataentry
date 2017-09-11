package org.hisp.dhis.android.dataentry.create;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class CreateItemsArgument implements Parcelable {
    public enum Type {
        TEI, EVENT, ENROLLMENT_EVENT, ENROLLMENT
    }

    @NonNull
    public abstract String uid();

    @NonNull
    public abstract String name();

    @NonNull
    public abstract Type type();

    // ToDo: remove this ugly hack
    @Nullable
    public abstract String enrollment();

    @NonNull
    public static CreateItemsArgument forEnrollmentEvent(@NonNull String uid,
            @NonNull String name, @NonNull String enrollment) {
        return new AutoValue_CreateItemsArgument(uid, name, Type.ENROLLMENT_EVENT, enrollment);
    }

    @NonNull
    public static CreateItemsArgument create(@NonNull String uid,
            @NonNull String name, @NonNull Type type) {
        return new AutoValue_CreateItemsArgument(uid, name, type, null);
    }
}
