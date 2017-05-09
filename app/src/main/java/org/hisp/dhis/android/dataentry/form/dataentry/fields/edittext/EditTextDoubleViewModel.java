package org.hisp.dhis.android.dataentry.form.dataentry.fields.edittext;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class EditTextDoubleViewModel extends EditTextModel<Double> {

    @NonNull
    public static EditTextDoubleViewModel create(@NonNull String uid, @NonNull String label,
            @NonNull Boolean mandatory, @Nullable Double value, @NonNull String hint) {
        return new AutoValue_EditTextDoubleViewModel(uid, label, mandatory,
                value, hint, 1, InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL |
                InputType.TYPE_NUMBER_FLAG_SIGNED);
    }
}
