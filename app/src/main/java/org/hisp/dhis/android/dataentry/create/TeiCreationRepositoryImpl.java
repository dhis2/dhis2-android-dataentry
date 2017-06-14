package org.hisp.dhis.android.dataentry.create;

import com.squareup.sqlbrite.BriteDatabase;

import io.reactivex.Observable;

class TeiCreationRepositoryImpl implements CreateItemsRepository {
    //TODO: implement this:
    public TeiCreationRepositoryImpl(BriteDatabase database) {
    }

    @Override
    public Observable<String> save(String entityOne, String entityTwo) {
        return null;
    }
}
