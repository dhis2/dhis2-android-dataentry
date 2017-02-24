package org.hisp.dhis.android.dataentry.commons.tuples;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Quartet<T> {

    @NonNull
    public abstract T val1();

    @NonNull
    public abstract T val2();

    @NonNull
    public abstract T val3();

    @NonNull
    public abstract T val4();

    @NonNull
    public static <T> Quartet<T> create(@NonNull T val1, @NonNull T val2,
            @NonNull T val3, @NonNull T val4) {
        return new AutoValue_Quartet<T>(val1, val2, val3, val4);
    }
}
