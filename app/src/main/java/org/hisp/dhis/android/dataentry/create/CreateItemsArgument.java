package org.hisp.dhis.android.dataentry.create;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.dataentry.selection.SelectionArgument;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@AutoValue
public abstract class CreateItemsArgument implements Parcelable {

    public enum Type {
        TEI, EVENT, ENROLLMENT_EVENT, ENROLLMENT
    }

    @NonNull
    public abstract String name();

    @NonNull
    public  abstract String uid();

    @NonNull
    public abstract Type type();

    @NonNull
    public abstract List<SelectionArgument.Type> selectorTypes();

    @NonNull
    public static CreateItemsArgument create(@NonNull String name, @NonNull String uid, @NonNull Type type) {
        SelectionArgument.Type firstType;
        SelectionArgument.Type secondType;

        if (type == Type.ENROLLMENT_EVENT) {
            firstType = SelectionArgument.Type.PROGRAM;
            secondType = SelectionArgument.Type.PROGRAM_STAGE;
        } else {
            firstType = SelectionArgument.Type.ORGANISATION;
            secondType = SelectionArgument.Type.PROGRAM;
        }
        return new AutoValue_CreateItemsArgument(name, uid, type,
                Collections.unmodifiableList(Arrays.asList(firstType, secondType)));
    }

}
