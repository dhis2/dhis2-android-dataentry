package org.hisp.dhis.android.dataentry.create;

import com.squareup.sqlbrite.BriteDatabase;

import io.reactivex.Observable;

class EventRepositoryImpl implements CreateItemsRepository {
    //TODO: implementation
    public EventRepositoryImpl(BriteDatabase database) {
    }

    @Override
    public Observable<String> save(String entityOne, String entityTwo) {
        return null;
    }
}
