package org.hisp.dhis.android.dataentry.commons.tuples;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Single<A> {

    @NonNull
    public abstract A val0();

    @NonNull
    public static <A> Single<A> create(@NonNull A val) {
        return new AutoValue_Single<>(val);
    }
}
