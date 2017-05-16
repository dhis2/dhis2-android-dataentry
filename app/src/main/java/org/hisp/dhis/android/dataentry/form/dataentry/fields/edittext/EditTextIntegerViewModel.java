package org.hisp.dhis.android.dataentry.form.dataentry.fields.edittext;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import static org.hisp.dhis.android.dataentry.commons.utils.StringUtils.isEmpty;

@AutoValue
public abstract class EditTextIntegerViewModel extends EditTextModel<Integer> {

    @NonNull
    public static EditTextIntegerViewModel fromRawValue(@NonNull String uid, @NonNull String label,
            @NonNull Boolean mandatory, @Nullable String value, @NonNull String hint, @NonNull Integer type) {
        return create(uid, label, mandatory, isEmpty(value) ? null : Integer.valueOf(value), hint, type);
    }

    @NonNull
    public static EditTextIntegerViewModel create(@NonNull String uid, @NonNull String label,
            @NonNull Boolean mandatory, @Nullable Integer value, @NonNull String hint, @NonNull Integer type) {
        return new AutoValue_EditTextIntegerViewModel(uid, label, mandatory, value, hint, 1, type);
    }
}
