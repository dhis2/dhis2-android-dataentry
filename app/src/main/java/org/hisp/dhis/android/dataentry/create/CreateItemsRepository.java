package org.hisp.dhis.android.dataentry.create;

import io.reactivex.Observable;

interface CreateItemsRepository {

    Observable<String> save(String entityOne, String entityTwo);

}
