package org.hisp.dhis.android.dataentry.user;

import io.reactivex.Observable;

public interface UserRepository {
    Observable<Boolean> isUserLoggedIn();
}
