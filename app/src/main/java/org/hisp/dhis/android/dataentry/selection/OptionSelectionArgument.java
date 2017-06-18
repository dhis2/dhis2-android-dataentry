package org.hisp.dhis.android.dataentry.selection;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.dataentry.form.dataentry.DataEntryArguments;

@AutoValue
public abstract class OptionSelectionArgument implements Parcelable {

    @NonNull
    public abstract DataEntryArguments dataEntryArgs();

    @NonNull
    public abstract String optionSet();

    @NonNull
    public abstract String field();

    @NonNull
    public abstract String fieldName();

    @NonNull
    public static OptionSelectionArgument create(@NonNull DataEntryArguments arguments,
            @NonNull String optionSet, @NonNull String field, @NonNull String fieldName) {
        return new AutoValue_OptionSelectionArgument(arguments, optionSet, field, fieldName);
    }
}
