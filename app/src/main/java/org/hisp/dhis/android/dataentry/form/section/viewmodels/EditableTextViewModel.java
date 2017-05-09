package org.hisp.dhis.android.dataentry.form.section.viewmodels;

import android.support.annotation.NonNull;

public abstract class EditableTextViewModel extends EditableViewModel {

    @NonNull
    public abstract String value();

}