package org.hisp.dhis.android.dataentry.form.dataentry.fields.edittext;

import android.support.annotation.NonNull;
import android.text.InputFilter;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.dataentry.form.dataentry.fields.EditableTextViewModel;

import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.safeUnmodifiableList;

@AutoValue
public abstract class EditTextViewModel extends EditableTextViewModel {

    @NonNull
    public abstract Integer inputType();

    @NonNull
    public abstract Integer maxLines();

    @NonNull
    public abstract String hint();

    @NonNull
    public abstract List<InputFilter> inputFilters();

    @NonNull
    public static EditTextViewModel create(@NonNull String uid,
            @NonNull String label, @NonNull Boolean mandatory, @NonNull String value,
            @NonNull Integer inputType, @NonNull Integer maxLines, @NonNull String hint,
            @NonNull List<InputFilter> inputFilters) {
        return new AutoValue_EditTextViewModel(uid, label, mandatory, value, inputType, maxLines, hint,
                safeUnmodifiableList(inputFilters));
    }
}