package org.hisp.dhis.android.dataentry.create;

import android.support.annotation.NonNull;

import io.reactivex.Observable;

interface CreateItemsRepository {

    @NonNull
    Observable<String> save(@NonNull String entityOne, @NonNull String entityTwo);
}
