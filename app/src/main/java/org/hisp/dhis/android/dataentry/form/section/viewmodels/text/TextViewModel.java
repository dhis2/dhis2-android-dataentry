package org.hisp.dhis.android.dataentry.form.section.viewmodels.text;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.dataentry.form.section.viewmodels.FormItemViewModel;

@AutoValue
public abstract class TextViewModel extends FormItemViewModel {

    @NonNull
    public abstract String value();

    @NonNull
    public static TextViewModel create(@NonNull String uid,
            @NonNull String label, @NonNull String value) {
        return new AutoValue_TextViewModel(uid, label, value);
    }
}