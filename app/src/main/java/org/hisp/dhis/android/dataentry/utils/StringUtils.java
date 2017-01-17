package org.hisp.dhis.android.dataentry.utils;

import android.support.annotation.Nullable;

public final class StringUtils {
    private StringUtils() {
        // no instances
    }

    public static boolean isEmpty(@Nullable CharSequence charSequence) {
        return charSequence != null && charSequence.length() == 0;
    }
}
