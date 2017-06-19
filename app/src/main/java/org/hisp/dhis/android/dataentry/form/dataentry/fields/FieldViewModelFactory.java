package org.hisp.dhis.android.dataentry.form.dataentry.fields;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.ValueType;

public interface FieldViewModelFactory {

    @NonNull
    FieldViewModel create(@NonNull String id, @NonNull String label, @NonNull ValueType valueType,
            @NonNull Boolean mandatory, @Nullable String optionSet, @Nullable String value);
}
