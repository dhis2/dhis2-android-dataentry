package org.hisp.dhis.android.dataentry.form.dataentry.fields.checkbox;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.dataentry.form.dataentry.fields.EditableFieldViewModel;

@AutoValue
public abstract class CheckBoxViewModel extends EditableFieldViewModel<Boolean> {

    @NonNull
    public static CheckBoxViewModel create(@NonNull String uid, @NonNull String label,
            @NonNull Boolean mandatory, @NonNull Boolean value) {
        return new AutoValue_CheckBoxViewModel(uid, label, mandatory, value);
    }
}