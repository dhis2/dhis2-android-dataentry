package org.hisp.dhis.android.dataentry.create;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.dataentry.selection.SelectionArgument;

/**
 * Represents the selectionStates of the selector in CreateItems.
 */
@AutoValue
abstract public class SelectionStateModel implements Parcelable {

    /**
     * Uid of the selection.
     *
     * @return
     */
    @NonNull
    abstract public String uid();

    /**
     * Name of the selection.
     *
     * @return
     */
    @NonNull
    abstract public String name();

    /**
     * Used for hint and later as heading.
     *
     * @return
     */
    @NonNull
    abstract public String label();

    @NonNull
    abstract public SelectionArgument.Type type();

    @NonNull
    public static SelectionStateModel create(@NonNull String uid, @NonNull String name, String label,
                                             SelectionArgument.Type type) {
        return new AutoValue_SelectionStateModel(uid, name, label, type);
    }

    @NonNull
    public static SelectionStateModel createModifiedSelection(@NonNull String uid, @NonNull String name,
                                                         @NonNull SelectionStateModel oldSelection) {
        return new AutoValue_SelectionStateModel(uid, name, oldSelection.label(), oldSelection.type());
    }

    public static SelectionStateModel createEmpty(String label, SelectionArgument.Type type) {
        return new AutoValue_SelectionStateModel("", "", label, type);
    }

}
