package org.hisp.dhis.android.dataentry.form.section.viewmodels.optionset;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.dataentry.form.section.viewmodels.EditableTextViewModel;

@AutoValue
public abstract class OptionSetViewModel extends EditableTextViewModel {

    @NonNull
    public abstract String hint();

    @NonNull
    public static OptionSetViewModel create(@NonNull String uid, @NonNull String label,
            @NonNull Boolean mandatory, @NonNull String value, @NonNull String hint) {
        return new AutoValue_OptionSetViewModel(uid, label, mandatory, value, hint);
    }
}