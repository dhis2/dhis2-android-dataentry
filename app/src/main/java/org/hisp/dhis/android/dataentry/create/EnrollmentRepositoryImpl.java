package org.hisp.dhis.android.dataentry.create;

import com.squareup.sqlbrite.BriteDatabase;

import io.reactivex.Observable;

class EnrollmentRepositoryImpl implements CreateItemsRepository {

    //TODO: implementation
    public EnrollmentRepositoryImpl(BriteDatabase database) {
    }

    @Override
    public Observable<String> save(String entityOne, String entityTwo) {
        return null;
    }
}
