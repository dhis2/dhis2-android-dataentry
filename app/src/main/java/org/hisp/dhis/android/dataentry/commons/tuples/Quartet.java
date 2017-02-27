package org.hisp.dhis.android.dataentry.commons.tuples;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Quartet<T> {

    @NonNull
    public abstract T val0();

    @NonNull
    public abstract T val1();

    @NonNull
    public abstract T val2();

    @NonNull
    public abstract T val3();

    @NonNull
    public static <T> Quartet<T> create(@NonNull T val0, @NonNull T val1,
            @NonNull T val2, @NonNull T val3) {
        return new AutoValue_Quartet<T>(val0, val1, val2, val3);
    }
}
