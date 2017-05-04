package org.hisp.dhis.android.dataentry.form.dataentry.viewmodels.date;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.dataentry.form.dataentry.viewmodels.EditableTextViewModel;

@AutoValue
public abstract class DateViewModel extends EditableTextViewModel {

    @NonNull
    public static DateViewModel create(@NonNull String uid, @NonNull String label,
            @NonNull Boolean mandatory, @NonNull String value) {
        return new AutoValue_DateViewModel(uid, label, mandatory, value);
    }
}
