package org.hisp.dhis.android.dataentry.form.dataentry.fields.edittext;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class EditTextIntegerViewModel extends EditTextModel<Integer> {

    @NonNull
    public static EditTextIntegerViewModel create(@NonNull String uid, @NonNull String label,
            @NonNull Boolean mandatory, @Nullable Integer value, @NonNull String hint, @NonNull Integer type) {
        return new AutoValue_EditTextIntegerViewModel(uid, label, mandatory, value, hint, 1, type);
    }
}
