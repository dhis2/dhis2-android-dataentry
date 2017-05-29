package org.hisp.dhis.android.dataentry.reports.search;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class SearchResultViewModel {

    @NonNull
    public abstract String uid();

    @NonNull
    public abstract String label();

    @NonNull
    public static SearchResultViewModel create(@NonNull String uid, @NonNull String name) {
        return new AutoValue_SearchResultViewModel(uid, name);
    }
}
