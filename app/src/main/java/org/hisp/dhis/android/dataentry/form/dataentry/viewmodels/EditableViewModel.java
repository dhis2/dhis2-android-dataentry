package org.hisp.dhis.android.dataentry.form.dataentry.viewmodels;

import android.support.annotation.NonNull;

public abstract class EditableViewModel extends FormItemViewModel {

    @NonNull
    public abstract Boolean mandatory();
}