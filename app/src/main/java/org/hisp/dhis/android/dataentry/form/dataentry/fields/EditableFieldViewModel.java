package org.hisp.dhis.android.dataentry.form.dataentry.fields;

import android.support.annotation.NonNull;

public abstract class EditableFieldViewModel<T> extends FieldViewModel {

    @NonNull
    public abstract Boolean mandatory();

    @NonNull
    public abstract T value();
}