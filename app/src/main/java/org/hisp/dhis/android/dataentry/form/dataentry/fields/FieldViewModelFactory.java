package org.hisp.dhis.android.dataentry.form.dataentry.fields;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.ValueType;

public interface FieldViewModelFactory {

    FieldViewModel fromCursor(Cursor cursor);

    FieldViewModel create(@NonNull String uid, @NonNull String label, @NonNull Boolean mandatory,
            @NonNull String value, @NonNull ValueType valueType, @Nullable String optionSet);
}
