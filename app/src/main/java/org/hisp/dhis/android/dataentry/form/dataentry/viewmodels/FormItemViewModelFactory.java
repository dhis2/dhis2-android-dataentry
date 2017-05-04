package org.hisp.dhis.android.dataentry.form.dataentry.viewmodels;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.ValueType;

public interface FormItemViewModelFactory {

    FormItemViewModel fromCursor(Cursor cursor);

    FormItemViewModel create(@NonNull String uid, @NonNull String label, @NonNull Boolean mandatory,
            @NonNull String value, @NonNull ValueType valueType,
            @Nullable String optionSet);
}
