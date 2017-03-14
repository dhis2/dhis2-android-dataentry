package org.hisp.dhis.android.dataentry.commons.utils;

public final class Preconditions {
    private Preconditions() {
        // no instances
    }

    public static <T> T isNull(T obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }

        return obj;
    }
}