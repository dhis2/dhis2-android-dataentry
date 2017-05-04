package org.hisp.dhis.android.dataentry.commons.utils;

import android.database.Cursor;
import android.support.annotation.NonNull;

public final class DbUtils {
    private DbUtils() {
        // no instances
    }

    @NonNull
    public static String string(@NonNull Cursor cursor, int column, @NonNull String fallback) {
        String value = cursor.getString(column);
        return value == null ? fallback : value;
    }
}
