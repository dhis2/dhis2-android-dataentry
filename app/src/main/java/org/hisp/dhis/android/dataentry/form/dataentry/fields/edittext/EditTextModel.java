package org.hisp.dhis.android.dataentry.form.dataentry.fields.edittext;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.form.dataentry.fields.EditableFieldViewModel;

abstract class EditTextModel<T> extends EditableFieldViewModel<T> {

    @NonNull
    public abstract String hint();

    @NonNull
    public abstract Integer maxLines();

    @NonNull
    public abstract Integer inputType();
}