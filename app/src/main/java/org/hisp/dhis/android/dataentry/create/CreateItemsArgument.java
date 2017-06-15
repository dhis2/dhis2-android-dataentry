package org.hisp.dhis.android.dataentry.create;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class CreateItemsArgument implements Parcelable {

    public enum Type {
        TEI, EVENT, ENROLMENT_EVENT, ENROLLMENT
    }

    @NonNull
    public abstract String name();

    @NonNull
    public  abstract String uid();

    @NonNull
    public abstract Type type();

    @NonNull
    public static CreateItemsArgument create(@NonNull String name, @NonNull String uid, @NonNull Type type) {
        return new AutoValue_CreateItemsArgument(name, uid, type);
    }

}