package org.hisp.dhis.android.dataentry.form.dataentry.fields.radiobutton;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.dataentry.form.dataentry.fields.EditableViewModel;

@AutoValue
public abstract class RadioButtonViewModel extends EditableViewModel {

    @Nullable
    public abstract Boolean value();

    @NonNull
    public static RadioButtonViewModel create(@NonNull String uid, @NonNull String label,
            @NonNull Boolean mandatory, @Nullable Boolean value) {
        return new AutoValue_RadioButtonViewModel(uid, label, mandatory, value);
    }
}